package fuzs.pickupnotifier.fabric.handler;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.config.ServerConfig;
import fuzs.pickupnotifier.network.S2CTakeItemMessage;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FabricItemPickupHandler {

    public static EventResult onEntityItemPickup(Player player, ItemEntity item) {

        if (PickUpNotifier.CONFIG.get(ServerConfig.class).partialPickUps && !item.isRemoved()) {

            // requires additional checks as it might actually not be possible for the item to be picked up
            if (!item.hasPickUpDelay() && (item.getOwner() == null || item.getOwner().equals(player.getUUID()))) {

                final ItemStack stack = item.getItem();
                int itemAmount = 0;
                if (player.getInventory().getFreeSlot() != -1) {

                    itemAmount = stack.getCount();
                } else {

                    int slotWithRemainingSpace = player.getInventory().getSlotWithRemainingSpace(stack);
                    if (slotWithRemainingSpace != -1) {

                        itemAmount = getSpaceAtIndex(player.getInventory(), slotWithRemainingSpace, stack);
                    }
                }

                if (itemAmount > 0) {

                    PickUpNotifier.NETWORK.sendTo((ServerPlayer) player, new S2CTakeItemMessage(item.getId(), itemAmount).toClientboundMessage());
                }
            }
        }

        return EventResult.PASS;
    }

    private static int getSpaceAtIndex(Inventory inventory, int slotIndex, ItemStack stack) {

        int itemCount = stack.getCount();
        ItemStack itemstack = inventory.getItem(slotIndex);
        int itemSpace = itemCount;
        if (itemCount > itemstack.getMaxStackSize() - itemstack.getCount()) {

            itemSpace = itemstack.getMaxStackSize() - itemstack.getCount();
        }

        if (itemSpace > inventory.getMaxStackSize() - itemstack.getCount()) {

            itemSpace = inventory.getMaxStackSize() - itemstack.getCount();
        }

        return itemSpace;
    }
}
