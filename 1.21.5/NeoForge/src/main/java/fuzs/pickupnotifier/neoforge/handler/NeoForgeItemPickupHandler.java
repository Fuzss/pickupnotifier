package fuzs.pickupnotifier.neoforge.handler;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.config.ServerConfig;
import fuzs.pickupnotifier.network.ClientboundTakeItemMessage;
import fuzs.pickupnotifier.network.ClientboundTakeItemStackMessage;
import fuzs.puzzleslib.api.network.v4.MessageSender;
import fuzs.puzzleslib.api.network.v4.PlayerSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

public class NeoForgeItemPickupHandler {
    private static ItemStack currentStack = ItemStack.EMPTY;

    public static void onEntityItemPickup$1(final ItemEntityPickupEvent.Pre evt) {
        if (PickUpNotifier.CONFIG.get(ServerConfig.class).backpackCompat) {
            ItemStack itemStack = evt.getItemEntity().getItem();
            if (!itemStack.isEmpty() && !evt.getItemEntity().isRemoved()) {
                currentStack = itemStack.copy();
            }
        }
    }

    public static void onEntityItemPickup$2(final ItemEntityPickupEvent.Pre evt) {
        if (!(evt.getPlayer() instanceof ServerPlayer serverPlayer)) return;
        ItemEntity itemEntity = evt.getItemEntity();
        Player player = evt.getPlayer();
        ItemStack stack = itemEntity.getItem();
        if (PickUpNotifier.CONFIG.get(ServerConfig.class).backpackCompat && evt.canPickup() != TriState.DEFAULT) {
            if (!currentStack.isEmpty()) {
                boolean sendTakeMessage = false;
                int takeAmount = currentStack.getCount() - stack.getCount();
                if (takeAmount > 0) {
                    currentStack.setCount(takeAmount);
                    sendTakeMessage = true;
                } else if (itemEntity.isRemoved()) {
                    sendTakeMessage = true;
                }
                if (sendTakeMessage) {
                    MessageSender.broadcast(PlayerSet.ofPlayer(serverPlayer),
                            new ClientboundTakeItemStackMessage(currentStack));
                }
            }
            currentStack = ItemStack.EMPTY;
        } else if (PickUpNotifier.CONFIG.get(ServerConfig.class).partialPickUps && !itemEntity.isRemoved()) {
            // requires additional checks as it might actually not be possible for the item to be picked up
            if (!itemEntity.hasPickUpDelay() &&
                    (itemEntity.getOwner() == null || itemEntity.lifespan - itemEntity.getAge() <= 200 ||
                            itemEntity.getOwner().equals(player.getUUID()))) {
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
                    MessageSender.broadcast(PlayerSet.ofPlayer(serverPlayer),
                            new ClientboundTakeItemMessage(itemEntity.getId(), itemAmount));
                }
            }
        }
    }

    public static void onPlayerItemPickup(final ItemEntityPickupEvent.Post evt) {
        if (!(evt.getPlayer() instanceof ServerPlayer serverPlayer)) return;
        if (!PickUpNotifier.CONFIG.get(ServerConfig.class).partialPickUps && !evt.getItemEntity().isRemoved()) {
            MessageSender.broadcast(PlayerSet.ofPlayer(serverPlayer),
                    new ClientboundTakeItemMessage(evt.getItemEntity().getId(), evt.getOriginalStack().getCount()));
        }
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
