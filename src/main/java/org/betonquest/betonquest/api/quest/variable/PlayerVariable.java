package org.betonquest.betonquest.api.quest.variable;

import org.betonquest.betonquest.api.profiles.Profile;

/**
 * Interface for quest-variables that are checked for a profile. It represents the normal variable as described in the
 * BetonQuest user documentation. It does not represent the "static" variant though, see {@link PlayerlessVariable}.
 */
public interface PlayerVariable {
    /**
     * Gets the resolved value for given profile.
     *
     * @param profile the {@link Profile} to get the value for
     * @return the value of this variable
     */
    String getValue(Profile profile);
}
