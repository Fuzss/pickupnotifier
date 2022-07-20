package fuzs.pickupnotifier.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface EntityItemPickupCallback {
    Event<EntityItemPickupCallback> EVENT = EventFactory.createArrayBacked(EntityItemPickupCallback.class, listeners -> (player, item) -> {
        for (EntityItemPickupCallback event : listeners) {
            event.onEntityItemPickup(player, item);
        }
    });

    /**
     * called when player collides with an item entity right before pickup checks are performed
     * this means the item actually being picked up is not guaranteed, vanilla checks still need to be done manually
     *
     * @param player the player attempting to pick up this item
     * @param item   the item entity being picked up
     */
    void onEntityItemPickup(Player player, ItemEntity item);
}
