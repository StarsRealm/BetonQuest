package org.betonquest.betonquest.utils;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Converts playerIDs to Player objects and back to playerIDs.
 */
@SuppressWarnings({"PMD.ClassNamingConventions", "PMD.CommentRequired"})
public final class PlayerConverter {

    private PlayerConverter() {
    }

    /**
     * Returns the {@link Profile} of the passed Player.
     *
     * @param player - Player object from which playerID needs to be extracted
     * @return playerID of the player
     */
    public static Profile getID(final OfflinePlayer player) {
        return new Profile() {
            @Override
            public OfflinePlayer getOfflinePlayer() {
                return player;
            }

            @Override
            public Optional<Player> getPlayer() {
                return Optional.ofNullable(player.getPlayer());
            }

            @Override
            public UUID getProfileUUID() {
                return player.getUniqueId();
            }

            @Override
            public String getProfileName() {
                return player.getName();
            }

            @SuppressWarnings("PMD.AvoidUncheckedExceptionsInSignatures")
            @Override
            public OnlineProfile getOnlineProfile() throws IllegalStateException {
                if (!(player instanceof Player)) {
                    throw new IllegalStateException("Player is Offline!");
                }
                return new OnlineProfile() {
                    @Override
                    public Player getOnlinePlayer() {
                        return (Player) player;
                    }

                    @Override
                    public OfflinePlayer getOfflinePlayer() {
                        return player;
                    }

                    @Override
                    public Optional<Player> getPlayer() {
                        return Optional.ofNullable(player.getPlayer());
                    }

                    @Override
                    public UUID getProfileUUID() {
                        return player.getUniqueId();
                    }

                    @Override
                    public String getProfileName() {
                        return player.getName();
                    }

                    @SuppressWarnings("PMD.AvoidUncheckedExceptionsInSignatures")
                    @Override
                    public OnlineProfile getOnlineProfile() throws IllegalStateException {
                        return this;
                    }

                    @Override
                    public boolean equals(final Object obj) {
                        return obj instanceof Profile profile && getProfileUUID().equals(profile.getProfileUUID());
                    }

                    @Override
                    public int hashCode() {
                        return Objects.hashCode(player.getUniqueId());
                    }
                };
            }

            @Override
            public boolean equals(final Object obj) {
                return obj instanceof Profile profile && getProfileUUID().equals(profile.getProfileUUID());
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(player.getUniqueId());
            }
        };
    }

    /**
     * Returns playerID of the player with passed name.
     *
     * @param name - name of the player from which playerID needs to be extracted
     * @return playerID of the player
     */
    @SuppressWarnings("deprecation")
    public static Profile getID(final String name) {
        return getID(Bukkit.getOfflinePlayer(name));
    }
}
