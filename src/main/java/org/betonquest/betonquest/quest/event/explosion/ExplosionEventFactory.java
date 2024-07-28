package org.betonquest.betonquest.quest.event.explosion;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadStaticEvent;

/**
 * Factory to create explosion events from {@link Instruction}s.
 */
public class ExplosionEventFactory implements EventFactory, StaticEventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the explosion event factory.
     *
     * @param data the data for primary server thread access
     */
    public ExplosionEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadEvent(createExplosionEvent(instruction), data);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadStaticEvent(createExplosionEvent(instruction), data);
    }

    private NullableEventAdapter createExplosionEvent(final Instruction instruction) throws InstructionParseException {
        final boolean setsFire = "1".equals(instruction.next());
        final boolean breaksBlocks = "1".equals(instruction.next());
        final VariableNumber power = instruction.getVarNum();
        final VariableLocation location = instruction.getLocation();
        return new NullableEventAdapter(new ExplosionEvent(location, power, setsFire, breaksBlocks));
    }
}
