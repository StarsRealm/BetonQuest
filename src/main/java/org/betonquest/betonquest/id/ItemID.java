package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PMD.CommentRequired")
public class ItemID extends ID {

    public ItemID(@Nullable final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier, "items", "Item");
    }

}
