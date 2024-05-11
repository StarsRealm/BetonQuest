package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.math.Tokenizer;
import org.betonquest.betonquest.utils.math.tokens.Token;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This variable evaluates the given calculation and returns the result.
 */
public class MathVariable extends Variable {

    /**
     * Regular expression that matches calculation expressions.
     * The regex has a named group 'expression' that contains only the math part without the identifier.
     */
    public static final Pattern CALC_REGEX = Pattern.compile("calc:(?<expression>.+)");

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The full calculation token.
     */
    @SuppressWarnings("deprecation")
    private final Token calculation;

    /**
     * Create a math variable from the given instruction.
     *
     * @param instruction instruction to parse
     * @throws InstructionParseException if the instruction is not a valid math variable
     */
    @SuppressWarnings("deprecation")
    public MathVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        staticness = true;
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        final Matcher expressionMatcher = CALC_REGEX.matcher(String.join(".", instruction.getAllParts()));
        if (!expressionMatcher.matches()) {
            throw new InstructionParseException("invalid format");
        }
        final String expression = expressionMatcher.group("expression");
        this.calculation = new Tokenizer(instruction.getPackage()).tokenize(expression);
    }

    @Override
    public String getValue(final Profile profile) {
        try {
            final double value = this.calculation.resolve(profile);
            if (value % 1 == 0) {
                return String.format(Locale.US, "%.0f", value);
            }
            return String.valueOf(value);
        } catch (final QuestRuntimeException e) {
            log.warn(instruction.getPackage(), "Could not calculate '" + calculation + "' (" + e.getMessage() + "). Returning 0 instead.", e);
            return "0";
        }
    }
}
