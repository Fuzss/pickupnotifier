package fuzs.pickupnotifier.handler;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.network.NetworkHandler;
import fuzs.pickupnotifier.network.message.S2CTakeItemMessage;
import fuzs.pickupnotifier.network.message.S2CTakeItemStackMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemPickupHandler {

    private ItemStack cachedStack = ItemStack.EMPTY;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityItemPickup1(final EntityItemPickupEvent evt) {

        if (PickUpNotifier.CONFIG.server().general().backpackCompat) {

            if (!evt.getItem().getItem().isEmpty() && !evt.getItem().isRemoved()) {

                this.cachedStack = evt.getItem().getItem().copy();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public void onEntityItemPickup2(final EntityItemPickupEvent evt) {

        ItemEntity entity = evt.getItem();
        Player player = evt.getPlayer();
        ItemStack stack = entity.getItem();
        if (PickUpNotifier.CONFIG.server().general().backpackCompat && evt.isCanceled()) {

            if (!this.cachedStack.isEmpty()) {

                boolean sendTakeMessage = false;
                int takeAmount = this.cachedStack.getCount() - stack.getCount();
                if (takeAmount > 0) {

                    this.cachedStack.setCount(takeAmount);
                    sendTakeMessage = true;
                } else if (evt.getItem().isRemoved()) {

                    sendTakeMessage = true;
                }

                if (sendTakeMessage) {

                    NetworkHandler.INSTANCE.sendTo(new S2CTakeItemStackMessage(this.cachedStack), (ServerPlayer) player);
                }
            }

            this.cachedStack = ItemStack.EMPTY;
        } else if (PickUpNotifier.CONFIG.server().general().partialPickUps && !evt.getItem().isRemoved()) {

            // requires additional checks as it might actually not be possible for the item to be picked up
            if (entity.getOwner() == null || entity.lifespan - entity.getAge() <= 200 || entity.getOwner().equals(player.getUUID())) {

                int itemAmount = 0;
                if (player.getInventory().getFreeSlot() != -1) {

                    itemAmount = stack.getCount();
                } else {

                    int slotWithRemainingSpace = player.getInventory().getSlotWithRemainingSpace(stack);
                    if (slotWithRemainingSpace != -1) {

                        itemAmount = this.getSpaceAtIndex(player.getInventory(), slotWithRemainingSpace, stack);
                    }
                }

                if (itemAmount > 0) {

                    NetworkHandler.INSTANCE.sendTo(new S2CTakeItemMessage(entity.getId(), itemAmount), (ServerPlayer) player);
                }
            }
        }
    }

    private int getSpaceAtIndex(Inventory inventory, int slotIndex, ItemStack stack) {

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

    @SubscribeEvent
    public void onPlayerItemPickup(final PlayerEvent.ItemPickupEvent evt) {

        if (!PickUpNotifier.CONFIG.server().general().partialPickUps && !evt.getOriginalEntity().isRemoved()) {

            NetworkHandler.INSTANCE.sendTo(new S2CTakeItemMessage(evt.getOriginalEntity().getId(), evt.getStack().getCount()), (ServerPlayer) evt.getPlayer());
        }
    }

}
