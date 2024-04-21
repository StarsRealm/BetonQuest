package org.betonquest.betonquest.conversation;

import com.starsrealm.starock.api.form.element.NpcDialogueButton;
import com.starsrealm.starock.form.NpcDialogueForm;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.compatibility.citizens.CitizensConversation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@SuppressWarnings("PMD.CommentRequired")
/**
 * Bedrock Dialog impl
 */
public class DialogIO implements ConversationIO {


    private final Entity entity;
    private final Player player;


    private final CitizensConversation conv;

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
        npcDialogueForm.hasNextForm(true).bindEntity(entity).dialogue(response).title(npcName);
        npcDialogueForm.handler((s, i) -> {
            conv.passPlayerAnswer(i);
        });

        npcDialogueForm.closeHandler(close -> {
            BetonQuest.getInstance().getLogger().info(close);
            conv.endConversation();
        });
    }

    @Override
    public void addPlayerOption(final String option) {
        final NpcDialogueButton button = new NpcDialogueButton();
        button.mode(NpcDialogueButton.ButtonMode.BUTTON_MODE);
        button.text(option);
        npcDialogueForm.buttons().add(button);
    }

    @Override
    public void display() {
        npcDialogueForm.send(player);
    }

    @Override
    public void clear() {
        npcDialogueForm = new NpcDialogueForm();
        npcDialogueForm.buttons(new ArrayList<>());
    }

    @Override
    public void end() {

    }
}
