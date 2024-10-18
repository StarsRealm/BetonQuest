package org.betonquest.betonquest.quest.condition.hunger;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * A condition that checks if the player's hunger level is at a certain level.
 */
public class HungerCondition implements OnlineCondition {

    /**
     * The hunger level required to pass the condition.
     */
    private final VariableNumber hunger;

    /**
     * Create a new hunger condition.
     *
     * @param hunger the hunger level required to pass the condition
     */
    public HungerCondition(final VariableNumber hunger) {
        this.hunger = hunger;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        return profile.getPlayer().getFoodLevel() >= hunger.getValue(profile).doubleValue();
    }
}
