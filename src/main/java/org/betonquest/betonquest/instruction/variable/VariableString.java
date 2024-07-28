package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a string that can contain variables.
 */
public class VariableString extends Variable<String> {
    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param pack  the package in which the variable is used in
     * @param input the string that may contain variables
     * @throws InstructionParseException if the variables could not be created or resolved to the given type
     * @deprecated use {@link #VariableString(VariableProcessor, QuestPackage, String)} instead
     */
    @Deprecated
    public VariableString(final QuestPackage pack, final String input) throws InstructionParseException {
        this(BetonQuest.getInstance().getVariableProcessor(), pack, input);
    }

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @throws InstructionParseException if the variables could not be created or resolved to the given type
     */
    public VariableString(final VariableProcessor variableProcessor, final QuestPackage pack, final String input) throws InstructionParseException {
        this(variableProcessor, pack, input, false);
    }

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param pack               the package in which the variable is used in
     * @param input              the string that may contain variables
     * @param replaceUnderscores whether underscores should be replaced
     * @throws InstructionParseException if the variables could not be created or resolved to the given type
     * @deprecated use {@link #VariableString(VariableProcessor, QuestPackage, String, boolean)} instead
     */
    @Deprecated
    public VariableString(final QuestPackage pack, final String input, final boolean replaceUnderscores) throws InstructionParseException {
        super(BetonQuest.getInstance().getVariableProcessor(), pack, replaceUnderscores(input, replaceUnderscores), (value) -> value);
    }

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param variableProcessor  the processor to create the variables
     * @param pack               the package in which the variable is used in
     * @param input              the string that may contain variables
     * @param replaceUnderscores whether underscores should be replaced
     * @throws InstructionParseException if the variables could not be created or resolved to the given type
     */
    public VariableString(final VariableProcessor variableProcessor, final QuestPackage pack, final String input, final boolean replaceUnderscores) throws InstructionParseException {
        super(variableProcessor, pack, replaceUnderscores(input, replaceUnderscores), (value) -> value);
    }

    private static String replaceUnderscores(final String input, final boolean replaceUnderscores) {
        if (replaceUnderscores) {
            return input.replaceAll("(?<!\\\\)_", " ").replaceAll("\\\\_", "_");
        }
        return input;
    }

    /**
     * Get the string value of the variable.
     *
     * @param profile the profile to get the value for
     * @return the string value of the variable
     * @deprecated use {@link #getValue(Profile)}} instead
     */
    @Deprecated
    public String getString(@Nullable final Profile profile) {
        try {
            return getValue(profile);
        } catch (final QuestRuntimeException e) {
            return "";
        }
    }
}
