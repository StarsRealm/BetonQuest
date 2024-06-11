package org.betonquest.betonquest.quest.event.lever;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.type.Switch;
import org.jetbrains.annotations.Nullable;

/**
 * This event turns on, of or toggles levers.
 */
public class LeverEvent implements ComposedEvent {

    /**
     * The type of state to set the lever to.
     */
    private final StateType stateType;

    /**
     * The location of the lever.
     */
    private final CompoundLocation location;

    /**
     * Create a new lever event.
     *
     * @param stateType the type of state to set the lever to
     * @param location  the location of the lever
     */
    public LeverEvent(final StateType stateType, final CompoundLocation location) {
        this.stateType = stateType;
        this.location = location;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        final Block block = location.getLocation(profile).getBlock();

        if (!block.getType().equals(Material.LEVER)) {
            throw new QuestRuntimeException("There is no lever at x: " + block.getX() + " y: " + block.getY() + " z: "
                    + block.getZ() + " in world '" + block.getWorld().getName() + "'");
        }

        final Switch lever = (Switch) block.getBlockData();
        lever.setPowered(stateType.apply(lever.isPowered()));
        block.setBlockData(lever);
        updateBlocksAround(block, lever);
    }

    private void updateBlocksAround(final Block block, final Switch powerableSwitch) {
        final BlockFace attachedTo = switch (powerableSwitch.getAttachedFace()) {
            case FLOOR -> BlockFace.DOWN;
            case CEILING -> BlockFace.UP;
            default -> powerableSwitch.getFacing().getOppositeFace();
        };
        final Block relative = block.getRelative(attachedTo);

        final BlockState relativeState = relative.getState();
        if (relativeState instanceof final Container container) {
            container.getInventory().clear();
        }
        relative.setType(Material.AIR, false);
        relativeState.update(true);
    }
}
