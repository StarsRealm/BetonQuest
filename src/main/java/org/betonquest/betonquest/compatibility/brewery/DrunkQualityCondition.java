package org.betonquest.betonquest.compatibility.brewery;

import com.dre.brewery.BPlayer;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

@SuppressWarnings("PMD.CommentRequired")
public class DrunkQualityCondition extends Condition {

    private final Integer quality;

    public DrunkQualityCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        quality = instruction.getInt();

        if (quality <= 0 || quality > 10) {
            throw new InstructionParseException("Drunk quality can only be between 1 and 10!");
        }
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final BPlayer bPlayer = BPlayer.get(profile.getOnlineProfile().get().getPlayer());
        return bPlayer != null && bPlayer.getQuality() >= quality;
    }
}
