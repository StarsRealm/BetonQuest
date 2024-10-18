package org.betonquest.betonquest.quest.condition.conversation;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConversationID;
import org.jetbrains.annotations.Nullable;

/**
 * Condition to check if a player is in a conversation or, if specified, in the specified conversation.
 */
public class InConversationCondition implements PlayerCondition {

    /**
     * Identifier of the conversation.
     */
    @Nullable
    private final ConversationID conversationID;

    /**
     * Constructor of the InConversationCondition.
     *
     * @param conversationID the conversation identifier
     */
    public InConversationCondition(@Nullable final ConversationID conversationID) {
        this.conversationID = conversationID;
    }

    @Override
    public boolean check(final Profile profile) throws QuestRuntimeException {
        final Conversation conversation = Conversation.getConversation(profile);
        return conversation != null && (conversationID == null || conversation.getID().equals(conversationID));
    }
}
