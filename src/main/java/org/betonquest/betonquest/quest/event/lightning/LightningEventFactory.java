package org.betonquest.betonquest.quest.event.lightning;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.ComposedEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadComposedEvent;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Factory for {@link LightningEvent} from the {@link Instruction}
 */
public class LightningEventFactory implements ComposedEventFactory {
    /**
     * Server to use for syncing to the primary server thread.
     */
    private final Server server;

    /**
     * Scheduler to use for syncing to the primary server thread.
     */
    private final BukkitScheduler scheduler;

    /**
     * Plugin to use for syncing to the primary server thread.
     */
    private final Plugin plugin;

    /**
     * Create a new LightningEventFactory.
     *
     * @param server    the server to use for syncing to the primary server thread
     * @param scheduler the scheduler to use for syncing to the primary server thread
     * @param plugin    the plugin to use for syncing to the primary server thread
     */
    public LightningEventFactory(final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public ComposedEvent parseComposedEvent(final Instruction instruction) throws InstructionParseException {
        final CompoundLocation location = instruction.getLocation();
        final boolean noDamage = instruction.hasArgument("noDamage");
        return new PrimaryServerThreadComposedEvent(
                new LightningEvent(location, noDamage),
                server, scheduler, plugin
        );
    }
}
