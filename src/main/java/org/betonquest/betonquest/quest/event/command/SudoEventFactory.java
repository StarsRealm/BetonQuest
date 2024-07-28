package org.betonquest.betonquest.quest.event.command;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Creates new {@link SudoEvent}s from {@link Instruction}s.
 */
public class SudoEventFactory extends BaseCommandEventFactory {
    /**
     * Create the sudo event factory.
     *
     * @param loggerFactory logger factory to use
     * @param data          the data for primary server thread access
     */
    public SudoEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        super(loggerFactory, data);
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new SudoEvent(parseCommands(instruction)),
                loggerFactory.create(SudoEvent.class),
                instruction.getPackage()
        ), data);
    }
}
