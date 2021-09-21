package fuzs.pickupnotifier.handler;

import fuzs.pickupnotifier.network.NetworkHandler;
import fuzs.pickupnotifier.network.message.S2CTakeItemMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemPickupHandler {

    private ItemStack cachedStack = ItemStack.EMPTY;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityItemPickup1(final EntityItemPickupEvent evt) {

        if (evt.isCanceled() && !evt.getItem().getItem().isEmpty()) {

            this.cachedStack = evt.getItem().getItem();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onEntityItemPickup2(final EntityItemPickupEvent evt) {

        if (evt.isCanceled() && evt.getItem().getItem().isEmpty() && !this.cachedStack.isEmpty()) {

            this.cachedStack = ItemStack.EMPTY;
        }
    }

    @SubscribeEvent
    public void onPlayerItemPickup(final PlayerEvent.ItemPickupEvent evt) {

        this.takeItem(evt.getPlayer(), evt.getOriginalEntity(), 0);
    }

    private void takeItem(Player player, Entity entity, int quantity) {

        if (!entity.isRemoved() && !player.level.isClientSide && (entity instanceof ItemEntity || entity instanceof AbstractArrow || entity instanceof ExperienceOrb)) {

            NetworkHandler.INSTANCE.sendTo(new S2CTakeItemMessage(entity.getId(), quantity), (ServerPlayer) player);
        }

    }

}
