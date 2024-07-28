package org.betonquest.betonquest.compatibility.auraskills;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.Utils;

@SuppressWarnings("PMD.CommentRequired")
public class AuraSkillsExperienceEvent extends QuestEvent {

    private final AuraSkillsApi auraSkills = AuraSkillsApi.get();

    private final VariableNumber amountVar;

    private final boolean isLevel;

    private final Skill skill;

    public AuraSkillsExperienceEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        final String skillName = instruction.next();
        amountVar = instruction.getVarNum();
        isLevel = instruction.hasArgument("level");

        final NamespacedId namespacedId = NamespacedId.fromDefault(skillName);
        skill = Utils.getNN(auraSkills.getGlobalRegistry().getSkill(namespacedId), "Invalid skill name");
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final SkillsUser user = auraSkills.getUser(profile.getPlayerUUID());

        if (user == null) {
            return null;
        }

        final int amount = amountVar.getInt(profile);

        if (isLevel) {
            final int currentLevel = user.getSkillLevel(skill);
            for (int i = 1; i <= amount; i++) {
                final double requiredXP = auraSkills.getXpRequirements().getXpRequired(skill, currentLevel + i);
                user.addSkillXpRaw(skill, requiredXP);
            }
        } else {
            user.addSkillXpRaw(skill, amount);
        }
        return null;
    }
}
