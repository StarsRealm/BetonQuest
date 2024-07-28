package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.placeholder.GlobalPlaceholder;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.jetbrains.annotations.Nullable;

/**
 * Defines HolographicDisplays global placeholder <code>{bqg:package:variable}</code>.
 */
public class HologramGlobalPlaceholder implements GlobalPlaceholder {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates new instance of HologramGlobalPlaceholder
     *
     * @param log the logger that will be used for logging
     */
    public HologramGlobalPlaceholder(final BetonQuestLogger log) {
        this.log = log;
    }

    @Override
    public int getRefreshIntervalTicks() {
        return 10 * 20;
    }

    @Override
    @Nullable
    public String getReplacement(@Nullable final String arguments) {
        if (arguments == null) {
            return "";
        }
        final int limit = 2;
        final String[] args = arguments.split(":", limit);
        if (args.length == limit) {
            return BetonQuest.getInstance().getVariableValue(args[0], "%" + args[1] + "%", null);
        }
        log.warn("Could not parse hologram variable " + arguments + "! " + "Expected format %<package>.<variable>%");
        return arguments;
    }
}
