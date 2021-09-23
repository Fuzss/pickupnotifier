package fuzs.pickupnotifier.handler;

import fuzs.pickupnotifier.config.ConfigValueHolder;
import fuzs.pickupnotifier.network.NetworkHandler;
import fuzs.pickupnotifier.network.message.S2CTakeItemMessage;
import fuzs.pickupnotifier.network.message.S2CTakeItemStackMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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

        if (ConfigValueHolder.getGeneralConfig().logEverything) {

            if (!evt.getItem().getItem().isEmpty()) {

                this.cachedStack = evt.getItem().getItem().copy();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public void onEntityItemPickup2(final EntityItemPickupEvent evt) {

        if (ConfigValueHolder.getGeneralConfig().logEverything) {

            ItemEntity entity = evt.getItem();
            Player player = evt.getPlayer();
            ItemStack stack = entity.getItem();
            if (evt.isCanceled()) {

                if (!this.cachedStack.isEmpty()) {

                    int takeAmount = this.cachedStack.getCount() - stack.getCount();
                    if (takeAmount > 0) {

                        this.cachedStack.setCount(takeAmount);
                        NetworkHandler.INSTANCE.sendTo(new S2CTakeItemStackMessage(this.cachedStack), (ServerPlayer) player);
                    }
                }

                this.cachedStack = ItemStack.EMPTY;
            } else {

                // requires additional checks as it might actually not be possible for the item to be picked up
                if (entity.getOwner() == null || entity.lifespan - entity.getAge() <= 200 || entity.getOwner().equals(player.getUUID())) {

                    if (player.getInventory().getFreeSlot() != -1) {

                        this.takeItem(player, entity, stack.getCount());
                    } else {

                        int slotWithRemainingSpace = player.getInventory().getSlotWithRemainingSpace(stack);
                        if (slotWithRemainingSpace != -1) {

                            this.takeItem(player, entity, this.getSpaceAtIndex(player.getInventory(), slotWithRemainingSpace, stack));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityItemPickup(final EntityItemPickupEvent evt) {

        ItemEntity itemEntity = evt.getItem();
        itemEntity.setItem(ItemStack.EMPTY);
        evt.setCanceled(true);
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

        if (!ConfigValueHolder.getGeneralConfig().logEverything) {

            this.takeItem(evt.getPlayer(), evt.getOriginalEntity(), evt.getStack().getCount());
        }
    }

    private void takeItem(Player player, Entity entity, int quantity) {

        if (!entity.isRemoved() && !player.level.isClientSide) {

            NetworkHandler.INSTANCE.sendTo(new S2CTakeItemMessage(entity.getId(), quantity), (ServerPlayer) player);
        }

    }

}
