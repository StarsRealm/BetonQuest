package org.betonquest.betonquest.quest.event.setblock;

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
import org.betonquest.betonquest.utils.BlockSelector;

/**
 * Factory to create setblock events from {@link Instruction}s.
 */
public class SetBlockEventFactory implements EventFactory, StaticEventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the setblock event factory.
     *
     * @param data the data for primary server thread access
     */
    public SetBlockEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadEvent(createSetBlockEvent(instruction), data);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadStaticEvent(createSetBlockEvent(instruction), data);
    }

    private NullableEventAdapter createSetBlockEvent(final Instruction instruction) throws InstructionParseException {
        final BlockSelector blockSelector = instruction.getBlockSelector(instruction.next());
        final VariableLocation variableLocation = instruction.getLocation();
        final boolean applyPhysics = !instruction.hasArgument("ignorePhysics");
        return new NullableEventAdapter(new SetBlockEvent(blockSelector, variableLocation, applyPhysics));
    }
}
