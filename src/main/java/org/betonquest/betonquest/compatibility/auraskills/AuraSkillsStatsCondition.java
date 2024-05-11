package org.betonquest.betonquest.compatibility.auraskills;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.Utils;

@SuppressWarnings("PMD.CommentRequired")
public class AuraSkillsStatsCondition extends Condition {

    private final AuraSkillsApi auraSkills = AuraSkillsApi.get();

    private final VariableNumber targetLevelVar;

    private final Stat stat;

    private final boolean mustBeEqual;

    public AuraSkillsStatsCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        final String statName = instruction.next();
        targetLevelVar = instruction.getVarNum();

        final NamespacedId namespacedId = NamespacedId.fromDefault(statName);
        stat = Utils.getNN(auraSkills.getGlobalRegistry().getStat(namespacedId), "Invalid stat name");

        mustBeEqual = instruction.hasArgument("equal");
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final SkillsUser user = auraSkills.getUser(profile.getPlayerUUID());

        if (user == null) {
            return false;
        }

        final double actualLevel = user.getStatLevel(stat);
        final double targetLevel = targetLevelVar.getDouble(profile);

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
