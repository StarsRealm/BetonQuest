package org.betonquest.betonquest.conversation;

import com.starsrealm.starock.api.form.element.NpcDialogueButton;
import com.starsrealm.starock.form.NpcDialogueForm;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.compatibility.citizens.CitizensConversation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
/**
 * Bedrock Dialog impl
 */
public class DialogIO implements ConversationIO {

    private final Entity entity;

    private final Player player;

    private final CitizensConversation conv;

    private final List<String> endConversations = new ArrayList<>();

    private NpcDialogueForm npcDialogueForm;

    public DialogIO(final Conversation conv, final OnlineProfile onlineProfile) {
        this.conv = (CitizensConversation) conv;
        this.entity = this.conv.getNPC().getEntity();
        this.player = onlineProfile.getPlayer();

        npcDialogueForm = new NpcDialogueForm();
        npcDialogueForm.buttons(new ArrayList<>());
    }

    @Override
    public void setNpcResponse(final String npcName, final String response) {
        if (response.endsWith("[player]")) {
            npcDialogueForm.hasNextForm(true).bindEntity(entity).dialogue(response.replace("[player]", "")).title(player.getName());
        } else {
            npcDialogueForm.hasNextForm(false).bindEntity(entity).dialogue(response).title(npcName);
        }

        npcDialogueForm.handler((s, i) -> {
            endConversations.add(s);
            conv.passPlayerAnswer(i + 1);
        });

        npcDialogueForm.closeHandler(close -> {
            if (!endConversations.contains(close)) {
                conv.endConversation();
            }
        });
    }

    @Override
    public void addPlayerOption(final String option) {
        final NpcDialogueButton button = new NpcDialogueButton();
        if (option.contains("[end]")) {
            button.setText(option.replace("[end]", ""));
            button.setHasNextForm(false);
        } else {
            button.setText(option);
            button.setHasNextForm(true);
        }
        button.setMode(NpcDialogueButton.ButtonMode.BUTTON_MODE);
        button.setCommands(new ArrayList<>());
        npcDialogueForm.buttons().add(button);
    }

    @Override
    public void display() {
        if (npcDialogueForm.buttons().getFirst().getText().contains("[pass]")) {
            npcDialogueForm.title(0 + npcDialogueForm.title());
        } else {
            npcDialogueForm.title(npcDialogueForm.buttons().size() + npcDialogueForm.title());
        }
        npcDialogueForm.send(player);
    }

    @Override
    public void clear() {
        npcDialogueForm = new NpcDialogueForm();
        npcDialogueForm.buttons(new ArrayList<>());
    }

    @Override
    public void end() {
        this.endConversations.clear();
    }
}
