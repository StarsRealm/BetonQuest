package org.betonquest.betonquest.quest.condition.time.real;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.condition.time.TimeFrame;

/**
 * Factory to create real time conditions from {@link Instruction}s.
 */
public class RealTimeConditionFactory implements PlayerlessConditionFactory {

    /**
     * Create the real time condition factory.
     */
    public RealTimeConditionFactory() {
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        final TimeFrame timeFrame = TimeFrame.parse(instruction.next());
        return new RealTimeCondition(timeFrame);
    }
}
