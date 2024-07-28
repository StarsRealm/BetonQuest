package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a number that can contain variables.
 */
public class VariableNumber extends Variable<Number> {
    /**
     * {@link Variable.ValueChecker} for numbers that must be greater than or equal to 0.
     */
    public static final ValueChecker<Number> NOT_LESS_THAN_ZERO_CHECKER = (value) -> {
        if (value.doubleValue() < 0) {
            throw new QuestRuntimeException("Value must be greater than or equal to 0: " + value);
        }
    };

    /**
     * {@link Variable.ValueChecker} for numbers that must be greater than or equal to 1.
     */
    public static final ValueChecker<Number> NOT_LESS_THAN_ONE_CHECKER = (value) -> {
        if (value.doubleValue() <= 0) {
            throw new QuestRuntimeException("Value must be greater than or equal to 1: " + value);
        }
    };

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param pack  the package in which the variable is used in
     * @param input the string that may contain variables
     * @throws InstructionParseException if the variables could not be created or resolved to the given type
     * @deprecated use {@link #VariableNumber(VariableProcessor, QuestPackage, String)} instead
     */
    @Deprecated
    public VariableNumber(final QuestPackage pack, final String input) throws InstructionParseException {
        this(BetonQuest.getInstance().getVariableProcessor(), pack, input, (value) -> {
        });
    }

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param pack         the package in which the variable is used in
     * @param input        the string that may contain variables
     * @param valueChecker the checker to verify valid values
     * @throws InstructionParseException if the variables could not be created or resolved to the given type
     * @deprecated use {@link #VariableNumber(VariableProcessor, QuestPackage, String, ValueChecker)} instead
     */
    @Deprecated
    public VariableNumber(final QuestPackage pack, final String input, final ValueChecker<Number> valueChecker) throws InstructionParseException {
        this(BetonQuest.getInstance().getVariableProcessor(), pack, input, valueChecker);
    }

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @throws InstructionParseException if the variables could not be created or resolved to the given type
     */
    public VariableNumber(final VariableProcessor variableProcessor, final QuestPackage pack, final String input)
            throws InstructionParseException {
        this(variableProcessor, pack, input, (value) -> {
        });
    }

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @param valueChecker      the checker to verify valid values
     * @throws InstructionParseException if the variables could not be created or resolved to the given type
     */
    public VariableNumber(final VariableProcessor variableProcessor, final QuestPackage pack, final String input,
                          final ValueChecker<Number> valueChecker) throws InstructionParseException {
        super(variableProcessor, pack, input, (value) -> {
            try {
                final double parsedValue = Double.parseDouble(value);
                valueChecker.check(parsedValue);
                return parsedValue;
            } catch (final NumberFormatException e) {
                throw new QuestRuntimeException("Could not parse number: " + value, e);
            }
        });
    }

    private Number getSaveValue(@Nullable final Profile profile) {
        try {
            return getValue(profile);
        } catch (final QuestRuntimeException e) {
            return 0;
        }
    }

    /**
     * Get the int value of the variable.
     *
     * @param profile the profile to get the value for
     * @return the int value of the variable
     * @deprecated use {@link #getValue(Profile)} and then {@link Number#intValue()} instead
     */
    @Deprecated
    public int getInt(@Nullable final Profile profile) {
        return getSaveValue(profile).intValue();
    }

    /**
     * Get the double value of the variable.
     *
     * @param profile the profile to get the value for
     * @return the double value of the variable
     * @deprecated use {@link #getValue(Profile)} and then {@link Number#doubleValue()} instead
     */
    @Deprecated
    public double getDouble(@Nullable final Profile profile) {
        return getSaveValue(profile).doubleValue();
    }
}
