package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.function.Function;

/**
 * Gives journal to the player.
 */
public class GiveJournalEvent implements OnlineEvent {
    /**
     * Function to get the player data for a given online profile.
     */
    private final Function<OnlineProfile, PlayerData> playerDataSource;

    /**
     * Creates a new GiveJournalEvent.
     *
     * @param playerDataSource source for the player data
     */
    public GiveJournalEvent(final Function<OnlineProfile, PlayerData> playerDataSource) {
        this.playerDataSource = playerDataSource;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestRuntimeException {
        playerDataSource.apply(profile).getJournal().addToInv();
    }
}
