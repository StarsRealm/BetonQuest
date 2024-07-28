package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;

/**
 * Factory to create {@link T}s from {@link Instruction}s.
 *
 * @param <T> legacy quest type
 */
public interface LegacyTypeFactory<T> {
    /**
     * Parse an instruction to create a {@link T}.
     *
     * @param instruction instruction to parse for the {@link T}
     * @return {@link T} represented by the instruction
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    T parseInstruction(Instruction instruction) throws InstructionParseException;
}
