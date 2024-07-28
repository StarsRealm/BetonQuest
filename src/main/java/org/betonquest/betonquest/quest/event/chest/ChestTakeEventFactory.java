package org.betonquest.betonquest.quest.event.chest;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadStaticEvent;

/**
 * Factory to create chest events from {@link Instruction}s.
 */
public class ChestTakeEventFactory implements EventFactory, StaticEventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the chest take event factory.
     *
     * @param data the data for primary server thread access
     */
    public ChestTakeEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadEvent(createChestTakeEvent(instruction), data);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadStaticEvent(createChestTakeEvent(instruction), data);
    }

    private NullableEventAdapter createChestTakeEvent(final Instruction instruction) throws InstructionParseException {
        final VariableLocation variableLocation = instruction.getLocation();
        final Instruction.Item[] item = instruction.getItemList();
        return new NullableEventAdapter(new ChestTakeEvent(variableLocation, item));
    }
}
