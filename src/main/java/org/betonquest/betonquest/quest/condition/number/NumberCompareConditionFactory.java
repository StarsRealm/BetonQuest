package org.betonquest.betonquest.quest.condition.number;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * The condition factory for the number compare condition.
 */
public class NumberCompareConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Creates the number compare condition factory.
     */
    public NumberCompareConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        return new NullableConditionAdapter(parse(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws InstructionParseException {
        return new NullableConditionAdapter(parse(instruction));
    }

    private NumberCompareCondition parse(final Instruction instruction) throws InstructionParseException {
        final VariableNumber first = instruction.getVarNum();
        final Operation operation = Operation.fromSymbol(instruction.next());
        final VariableNumber second = instruction.getVarNum();
        return new NumberCompareCondition(first, second, operation);
    }
}
