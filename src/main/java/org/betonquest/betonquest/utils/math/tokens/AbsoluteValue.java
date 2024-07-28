package org.betonquest.betonquest.utils.math.tokens;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.jetbrains.annotations.Nullable;

/**
 * Returns the absolute value (see {@link Math#abs(double)}) of a token.
 *
 * @deprecated This should be replaced in BQ 2.0 with a real expression parsing lib like
 * <a href="https://github.com/fasseg/exp4j">fasseg/exp4j</a>
 */
@Deprecated
public class AbsoluteValue implements Token {

    /**
     * Token that is inside.
     */
    private final Token inside;

    /**
     * Constructs a new absolute value.
     *
     * @param inside token that is inside
     */
    public AbsoluteValue(final Token inside) {
        this.inside = inside;
    }

    @Override
    public double resolve(@Nullable final Profile profile) throws QuestRuntimeException {
        return Math.abs(inside.resolve(profile));
    }

    @Override
    public String toString() {
        return '|' + inside.toString() + '|';
    }
}
