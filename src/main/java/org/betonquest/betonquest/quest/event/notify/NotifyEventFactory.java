package org.betonquest.betonquest.quest.event.notify;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.notify.Notify;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory for {@link NotifyEvent}.
 */
public class NotifyEventFactory implements EventFactory {
    /**
     * A pattern for the notation of notifyIO options.
     */
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(?<key>[a-zA-Z]+?):(?<value>\\S+)");

    /**
     * A pattern for the notation of multiple translations in a single event.
     */
    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("\\{(?<lang>[a-z-]{2,5})}\\s(?<message>.*?)(?=\\s+\\{[a-z-]{2,5}}\\s|$)");

    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Variable processor for variables in messages.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Creates a new factory for {@link NotifyEvent}.
     *
     * @param loggerFactory     the logger factory to use for creating the event logger
     * @param data              the data for primary server thread access
     * @param variableProcessor the variable processor for creating variables
     */
    public NotifyEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data, final VariableProcessor variableProcessor) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final Map<String, VariableString> translations = new HashMap<>();
        final NotifyIO notifyIO = processInstruction(instruction, translations);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new NotifyEvent(notifyIO, translations),
                loggerFactory.create(NotifyEvent.class),
                instruction.getPackage()
        ), data);
    }

    /**
     * Processes the instruction and extracts the translations and returns the notifyIO to use.
     *
     * @param instruction  the instruction to process
     * @param translations the map to put the translations into
     * @return the notifyIO to use
     * @throws InstructionParseException if the instruction is invalid
     */
    protected NotifyIO processInstruction(final Instruction instruction, final Map<String, VariableString> translations) throws InstructionParseException {
        final String rawInstruction = String.join(" ", instruction.getAllParts());

        final Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(rawInstruction);
        final int indexEnd = keyValueMatcher.find() ? keyValueMatcher.start() : rawInstruction.length();
        keyValueMatcher.reset();

        final String langMessages = rawInstruction.substring(0, indexEnd);

        translations.putAll(getLanguages(instruction.getPackage(), langMessages));
        final Map<String, String> data = getData(keyValueMatcher);

        final String category = data.remove("category");
        return Notify.get(instruction.getPackage(), category, data);
    }

    private Map<String, VariableString> getLanguages(final QuestPackage pack, final String messages) throws InstructionParseException {
        final Map<String, VariableString> translations = new HashMap<>();
        final Matcher languageMatcher = LANGUAGE_PATTERN.matcher(messages);

        while (languageMatcher.find()) {
            final String lang = languageMatcher.group("lang");
            final String message = languageMatcher.group("message")
                    .replace("\\{", "{")
                    .replace("\\:", ":");
            translations.put(lang, new VariableString(variableProcessor, pack, message));
        }

        final String defaultLanguageKey = Config.getLanguage();
        if (translations.isEmpty()) {
            final String message = messages
                    .replace("\\{", "{")
                    .replace("\\:", ":");
            translations.put(defaultLanguageKey, new VariableString(variableProcessor, pack, message));
        }
        if (!translations.containsKey(defaultLanguageKey)) {
            throw new InstructionParseException("No message defined for default language '" + defaultLanguageKey + "'!");
        }
        return translations;
    }

    private Map<String, String> getData(final Matcher keyValueMatcher) {
        final Map<String, String> data = new HashMap<>();

        while (keyValueMatcher.find()) {
            final String key = keyValueMatcher.group("key");
            final String value = keyValueMatcher.group("value");
            data.put(key, value);
        }

        data.remove("events");
        data.remove("conditions");

        return data;
    }
}
