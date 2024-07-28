package org.betonquest.betonquest.quest.event.party;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Fires specified events for every player in the party.
 */
public class PartyEventFactory implements EventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a PartyEventFactory instance.
     *
     * @param loggerFactory logger factory to use
     */
    public PartyEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final VariableNumber range = instruction.getVarNum();
        final VariableNumber amount = instruction.getVarNum(instruction.getOptional("amount"));
        final ConditionID[] conditions = instruction.getList(instruction::getCondition).toArray(new ConditionID[0]);
        final EventID[] events = instruction.getList(instruction::getEvent).toArray(new EventID[0]);
        return new OnlineEventAdapter(
                new PartyEvent(range, amount, conditions, events),
                loggerFactory.create(PartyEvent.class),
                instruction.getPackage()
        );
    }
}
