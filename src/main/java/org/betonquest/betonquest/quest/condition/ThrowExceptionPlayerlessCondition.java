package org.betonquest.betonquest.quest.condition;

import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * A playerless condition placeholder that throws an exception when checked.
 */
public class ThrowExceptionPlayerlessCondition implements PlayerlessCondition {

    /**
     * Create a playerless condition that throws an exception when checked.
     */
    public ThrowExceptionPlayerlessCondition() {
    }

    @Override
    public boolean check() throws QuestRuntimeException {
        throw new QuestRuntimeException("This condition cannot be checked in the current context.");
    }
}
