package org.betonquest.betonquest.quest.condition.scoreboard;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create scoreboard conditions from {@link Instruction}s.
 */
public class ScoreboardConditionFactory implements PlayerConditionFactory {

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the scoreboard condition factory.
     *
     * @param data the data used for checking the condition on the main thread
     */
    public ScoreboardConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final String objective = instruction.next();
        final VariableNumber count = instruction.getVarNum();
        return new PrimaryServerThreadPlayerCondition(new ScoreboardCondition(objective, count), data);
    }
}
