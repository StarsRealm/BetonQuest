package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create a specific {@link PlayerlessCondition} from {@link Instruction}s.
 */
public interface PlayerlessConditionFactory extends PlayerlessQuestFactory<PlayerlessCondition> {
    /**
     * Parses an instruction to create a {@link PlayerlessCondition}.
     *
     * @param instruction instruction to parse
     * @return condition represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    @Override
    PlayerlessCondition parsePlayerless(Instruction instruction) throws InstructionParseException;
}
