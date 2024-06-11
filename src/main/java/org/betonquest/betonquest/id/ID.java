package org.betonquest.betonquest.id;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Identifies any object(events, objectives, conversations etc.) of BetonQuest's scripting system via the path syntax.
 * Handles relative and absolute paths.
 */
@SuppressWarnings({"PMD.ShortClassName", "PMD.AbstractClassWithoutAbstractMethod"})
public abstract class ID {

    /**
     * The string used to go "up the hierarchy" in relative paths.
     */
    public static final String UP_STR = "_";

    /**
     * A list of all objects that can be addressed via this ID.
     */
    public static final List<String> PATHS = List.of("events", "conditions", "objectives", "variables",
            "conversations", "cancel", "items");

    /**
     * The identifier of the object without the package name.
     */
    protected String identifier;

    /**
     * The package the object is in.
     */
    protected QuestPackage pack;

    /**
     * The created instruction of the object.
     */
    @Nullable
    protected Instruction instruction;

    /**
     * Creates a new ID. Handles relative and absolute paths and edge cases with special IDs like variables.
     *
     * @param pack       the package the ID is in
     * @param identifier the id instruction string
     * @throws ObjectNotFoundException if the ID could not be parsed
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    protected ID(@Nullable final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        if (identifier.isEmpty()) {
            throw new ObjectNotFoundException("ID is null");
        }
        if (identifier.contains(".")) {
            int dotIndex = identifier.indexOf('.');
            final String packName = identifier.substring(0, dotIndex);
            if (pack != null && packName.startsWith(UP_STR + "-")) {
                resolveRelativePathUp(pack, identifier, packName);
            } else if (pack != null && packName.startsWith("-")) {
                resolveRelativePathDown(pack, identifier, packName);
            } else {
                dotIndex = getDotIndex(pack, identifier, packName, dotIndex);
            }
            if (identifier.length() == dotIndex + 1) {
                throw new ObjectNotFoundException("ID of the pack '" + this.pack + "' is null");
            }
            this.identifier = identifier.substring(dotIndex + 1);
        } else {
            if (pack == null) {
                throw new ObjectNotFoundException("No package specified for id '" + identifier + "'!");
            }
            this.pack = pack;
            this.identifier = identifier;
        }

        if (this.pack == null) {
            throw new ObjectNotFoundException("Package in ID '" + identifier + "' does not exist");
        }
    }

    /**
     * Constructor of an id that also create an instruction.
     *
     * @param pack       the package the ID is in
     * @param identifier the id instruction string
     * @param section    the section of the config file
     * @param readable   the readable name of the object
     * @throws ObjectNotFoundException if the ID could not be parsed
     */
    protected ID(@Nullable final QuestPackage pack, final String identifier, final String section, final String readable) throws ObjectNotFoundException {
        this(pack, identifier);
        final String rawInstruction = this.pack.getString(section + "." + this.identifier);
        if (rawInstruction == null) {
            throw new ObjectNotFoundException(readable + " '" + getFullID() + "' is not defined");
        }
        instruction = new Instruction(BetonQuest.getInstance().getLoggerFactory().create(Instruction.class), this.pack, this, rawInstruction);
    }

    private int getDotIndex(@Nullable final QuestPackage pack, final String identifier, final String packName, final int dotIndex) {
        final String[] parts = identifier.split(":")[0].split("\\.");
        final QuestPackage potentialPack = Config.getPackages().get(packName);
        if (potentialPack == null) {
            this.pack = pack;
            return -1;
        }
        if (BetonQuest.isVariableType(packName)) {
            return resolveIdOfVariable(pack, parts, potentialPack, dotIndex);
        }
        this.pack = potentialPack;
        return dotIndex;
    }

    private void resolveRelativePathUp(final QuestPackage pack, final String identifier, final String packName) throws ObjectNotFoundException {
        final String[] root = pack.getQuestPath().split("-");
        final String[] path = packName.split("-");
        int stepsUp = 0;
        while (stepsUp < path.length && UP_STR.equals(path[stepsUp])) {
            stepsUp++;
        }
        if (stepsUp > root.length) {
            throw new ObjectNotFoundException("Relative path goes out of package scope! Consider removing a few '"
                    + UP_STR + "'s in ID " + identifier);
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < root.length - stepsUp; i++) {
            builder.append(root[i]).append('-');
        }
        for (int i = stepsUp; i < path.length; i++) {
            builder.append(path[i]).append('-');
        }
        final String absolute = builder.substring(0, builder.length() - 1);
        this.pack = Config.getPackages().get(absolute);
        if (this.pack == null) {
            throw new ObjectNotFoundException("Relative path in ID '" + identifier + "' resolved to '"
                    + absolute + "', but this package does not exist!");
        }
    }

    private void resolveRelativePathDown(final QuestPackage pack, final String identifier, final String packName) throws ObjectNotFoundException {
        final String currentPath = pack.getQuestPath();
        final String fullPath = currentPath + packName;

        this.pack = Config.getPackages().get(fullPath);
        if (this.pack == null) {
            throw new ObjectNotFoundException("Relative path in ID '" + identifier + "' resolved to '"
                    + fullPath + "', but this package does not exist!");
        }
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private int resolveIdOfVariable(final QuestPackage pack, final String[] parts, final QuestPackage potentialPack, final int dotIndex) {
        int index = dotIndex;
        if (parts.length == 2 && isIdFromPack(potentialPack, parts[1])) {
            this.pack = potentialPack;
        } else if (parts.length > 2) {
            if (BetonQuest.isVariableType(parts[1]) && isIdFromPack(potentialPack, parts[2])) {
                this.pack = potentialPack;
            } else if (isIdFromPack(potentialPack, parts[1])) {
                this.pack = pack;
                index = -1;
            } else {
                this.pack = potentialPack;
            }
        } else {
            this.pack = pack;
            index = -1;
        }
        return index;
    }

    /**
     * Checks if an ID belongs to a provided QuestPackage. This checks all events, conditions, objectives and variables
     * for any ID matching the provided string
     *
     * @param pack       The quest package to search
     * @param identifier The id
     * @return true if the id exists in the quest package
     */
    private boolean isIdFromPack(final QuestPackage pack, final String identifier) {
        final MultiConfiguration config = pack.getConfig();
        for (final String path : PATHS) {
            if (config.getString(path + "." + identifier, null) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the package the object exist in.
     *
     * @return the package
     */
    public final QuestPackage getPackage() {
        return pack;
    }

    /**
     * Returns the base ID of the object. This is the ID without the package name.
     *
     * @return the base ID
     */
    public final String getBaseID() {
        return identifier;
    }

    /**
     * Returns the full ID of the object, which is in this format: <br>
     * <code>pack.identifier</code>
     *
     * @return the full ID
     */
    public final String getFullID() {
        return pack.getQuestPath() + "." + getBaseID();
    }

    /**
     * Returns the instruction of the object.
     *
     * @return the instruction
     * @throws IllegalStateException if the instruction is not set
     */
    public Instruction getInstruction() {
        if (instruction == null) {
            throw new IllegalStateException("Instruction is not set for ID " + getFullID());
        }
        return instruction;
    }

    @Override
    public String toString() {
        return getFullID();
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ID other = (ID) obj;
        return Objects.equals(identifier, other.identifier)
                && Objects.equals(pack.getQuestPath(), other.pack.getQuestPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, pack.getQuestPath());
    }
}
