package org.betonquest.betonquest.quest.registry;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.modules.schedule.EventScheduling;
import org.betonquest.betonquest.quest.event.legacy.QuestEventFactory;
import org.betonquest.betonquest.quest.registry.processor.CancellerProcessor;
import org.betonquest.betonquest.quest.registry.processor.ConditionProcessor;
import org.betonquest.betonquest.quest.registry.processor.ConversationProcessor;
import org.betonquest.betonquest.quest.registry.processor.EventProcessor;
import org.betonquest.betonquest.quest.registry.processor.ObjectiveProcessor;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

import java.util.Collection;
import java.util.Map;

/**
 * Stores the active Quest Types, Conversations, Quest Canceller and Event Scheduler.
 */
public class QuestRegistry {
    /**
     * The custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Event scheduling module.
     */
    private final EventScheduling eventScheduling;

    /**
     * Condition logic.
     */
    private final ConditionProcessor conditionProcessor;

    /**
     * Event logic.
     */
    private final EventProcessor eventProcessor;

    /**
     * Objective logic.
     */
    private final ObjectiveProcessor objectiveProcessor;

    /**
     * Variable logic.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Quest Canceller logic.
     */
    private final CancellerProcessor cancellerProcessor;

    /**
     * Conversation Data logic.
     */
    private final ConversationProcessor conversationProcessor;

    /**
     * Create a new Registry for storing and using Conditions, Events, Objectives, Variables,
     * Conversations and Quest canceller.
     *
     * @param log            the custom logger for this registry
     * @param loggerFactory  the logger factory used for new custom logger instances
     * @param plugin         the plugin used to create new conversation data
     * @param scheduleTypes  the available schedule types
     * @param conditionTypes the available condition types
     * @param eventTypes     the available event types
     * @param objectiveTypes the available objective types
     * @param variableTypes  the available variable types
     */
    public QuestRegistry(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory, final BetonQuest plugin,
                         final Map<String, EventScheduling.ScheduleType<?>> scheduleTypes,
                         final Map<String, Class<? extends Condition>> conditionTypes, final Map<String, QuestEventFactory> eventTypes,
                         final Map<String, Class<? extends Objective>> objectiveTypes, final Map<String, Class<? extends Variable>> variableTypes) {
        this.log = log;
        this.eventScheduling = new EventScheduling(loggerFactory.create(EventScheduling.class, "Schedules"), scheduleTypes);
        this.conditionProcessor = new ConditionProcessor(loggerFactory.create(ConditionProcessor.class), conditionTypes);
        this.eventProcessor = new EventProcessor(loggerFactory.create(EventProcessor.class), eventTypes);
        this.objectiveProcessor = new ObjectiveProcessor(loggerFactory.create(ObjectiveProcessor.class), objectiveTypes);
        this.variableProcessor = new VariableProcessor(loggerFactory.create(VariableProcessor.class), variableTypes, loggerFactory);
        this.cancellerProcessor = new CancellerProcessor(loggerFactory.create(CancellerProcessor.class));
        this.conversationProcessor = new ConversationProcessor(loggerFactory.create(ConversationProcessor.class), plugin);
    }

    /**
     * Loads Conditions, Events, Objectives, Variables, Conversations, Quest Canceller and Event Scheduler.
     * <p>
     * Removes previous data and loads the given QuestPackages.
     *
     * @param packages the quest packages to load
     */
    public void loadData(final Collection<QuestPackage> packages) {
        eventScheduling.stopAll();
        conditionProcessor.clear();
        eventProcessor.clear();
        objectiveProcessor.clear();
        variableProcessor.clear();
        cancellerProcessor.clear();
        conversationProcessor.clear();

        for (final QuestPackage pack : packages) {
            final String packName = pack.getQuestPath();
            log.debug(pack, "Loading stuff in package " + packName);
            cancellerProcessor.load(pack);
            eventProcessor.load(pack);
            conditionProcessor.load(pack);
            objectiveProcessor.load(pack);
            conversationProcessor.load(pack);
            eventScheduling.loadData(pack);

            log.debug(pack, "Everything in package " + packName + " loaded");
        }

        conversationProcessor.checkExternalPointers();

        log.info("There are " + conditionProcessor.size() + " conditions, " + eventProcessor.size() + " events, "
                + objectiveProcessor.size() + " objectives and " + conversationProcessor.size() + " conversations loaded from "
                + packages.size() + " packages.");

        eventScheduling.startAll();
    }

    /**
     * Gets the bstats metric supplier for registered and active quest types.
     *
     * @return instruction metrics for conditions, events, objectives and variables
     */
    public Map<String, InstructionMetricsSupplier<? extends ID>> metricsSupplier() {
        return Map.ofEntries(
                conditionProcessor.metricsSupplier(),
                eventProcessor.metricsSupplier(),
                objectiveProcessor.metricsSupplier(),
                variableProcessor.metricsSupplier()
        );
    }

    /**
     * Stops the {@link EventScheduling} module.
     */
    public void stopAllEventSchedules() {
        eventScheduling.stopAll();
    }

    /**
     * Gets the class processing condition logic.
     *
     * @return condition logic
     */
    public ConditionProcessor conditions() {
        return conditionProcessor;
    }

    /**
     * Gets the class processing event logic.
     *
     * @return event logic
     */
    public EventProcessor events() {
        return eventProcessor;
    }

    /**
     * Gets the class processing objective logic.
     *
     * @return objective logic
     */
    public ObjectiveProcessor objectives() {
        return objectiveProcessor;
    }

    /**
     * Gets the class processing variable logic.
     *
     * @return variable logic
     */
    public VariableProcessor variables() {
        return variableProcessor;
    }

    /**
     * Gets the class processing quest canceller logic.
     *
     * @return canceller logic
     */
    public CancellerProcessor questCanceller() {
        return cancellerProcessor;
    }

    /**
     * Gets the class processing quest conversation logic.
     *
     * @return conversation logic
     */
    public ConversationProcessor conversations() {
        return conversationProcessor;
    }
}
