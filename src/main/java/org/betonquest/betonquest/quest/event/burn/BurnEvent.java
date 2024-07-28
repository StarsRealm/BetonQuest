package org.betonquest.betonquest.quest.event.burn;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * The burn event. Sets the player on fire.
 */
public class BurnEvent implements OnlineEvent {
    /**
     * Duration of the burn effect.
     */
    private final VariableNumber duration;

    /**
     * Create a burn event that sets the player on fire for the given duration.
     *
     * @param duration duration of burn
     */
    public BurnEvent(final VariableNumber duration) {
        this.duration = duration;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestRuntimeException {
        profile.getPlayer().setFireTicks(duration.getValue(profile).intValue() * 20);
    }
}
