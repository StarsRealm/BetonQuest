package org.betonquest.betonquest.quest.event.kill;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Kills the player.
 */
public class KillEvent implements OnlineEvent {

    /**
     * Creates a new kill event.
     */
    public KillEvent() {
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestRuntimeException {
        profile.getPlayer().setHealth(0);
    }
}
