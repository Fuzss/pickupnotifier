package com.fuzs.pickupnotifier.handler;

import com.fuzs.pickupnotifier.util.DisplayEntry;
import com.fuzs.pickupnotifier.util.PickUpEntry;
import com.fuzs.pickupnotifier.util.PositionPreset;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class HudEventHandler {

    private final Minecraft mc = Minecraft.getInstance();

    private final List<PickUpEntry> pickups = Lists.newArrayList();
    private final List<DisplayEntry> displays = Lists.newArrayList();
    private boolean dirty;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent evt) {

        if (evt.phase != TickEvent.Phase.END || this.mc.isGamePaused()) {
            return;
        }
        synchronized (this.pickups) {
            this.pickups.forEach(it -> it.tick(evt.renderTickTime));
            if (this.pickups.removeIf(PickUpEntry::isDead)) {
                this.dirty = true;
            }
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityItemPickup(EntityItemPickupEvent evt) {

        if (ConfigBuildHandler.GENERAL_CONFIG.logEverything.get()) {
            EntityItem item = evt.getItem();
            EntityPlayer player = evt.getEntityPlayer();
            // requires additional checks as it might actually not be possible for the item to be picked up
            boolean owner = item.getOwnerId() == null || item.lifespan - item.getAge() <= 200 || item.getOwnerId().equals(player.getUniqueID());
            if (owner && (player.inventory.getFirstEmptyStack() != -1 || player.inventory.getSlotFor(item.getItem()) != -1)) {
                this.addPickUpEntry(item.getItem());
            }
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemPickup(PlayerEvent.ItemPickupEvent evt) {

        if (!ConfigBuildHandler.GENERAL_CONFIG.logEverything.get()) {
            this.addPickUpEntry(evt.getStack());
        }

    }

    private void addPickUpEntry(ItemStack stack) {

        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(stack.getItem());
        boolean blacklisted = resourcelocation != null && (ConfigBuildHandler.GENERAL_CONFIG.blacklist.get().contains(resourcelocation.toString())
                || ConfigBuildHandler.GENERAL_CONFIG.blacklist.get().contains(resourcelocation.getNamespace()));

        if (!stack.isEmpty() && stack.getCount() > 0 && !blacklisted) {
            synchronized (this.pickups) {
                this.pickups.add(new PickUpEntry(stack.copy(), ConfigBuildHandler.GENERAL_CONFIG.displayTime.get()));
                this.dirty = true;
            }
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlayText(RenderGameOverlayEvent.Text evt) {

        if (this.pickups.isEmpty()) {
            return;
        }

        float scale = ConfigBuildHandler.DISPLAY_CONFIG.scale.get() / 6.0F;
        int scaledWidth = (int) (this.mc.mainWindow.getScaledWidth() / scale);
        int scaledHeight = (int) (this.mc.mainWindow.getScaledHeight() / scale);

        if (this.dirty) {
            this.displays.clear();
            int length = (int) (scaledHeight * ConfigBuildHandler.DISPLAY_CONFIG.height.get().floatValue() / DisplayEntry.HEIGHT) - 1;
            List<PickUpEntry> pickupsCopy = Lists.newArrayList(this.pickups);
            Collections.reverse(pickupsCopy);
            for (PickUpEntry pickUpEntry : pickupsCopy) {
                Optional<DisplayEntry> displayEntry = ConfigBuildHandler.GENERAL_CONFIG.combineEntries.get() ? this.displays
                        .stream().filter(it -> it.compareItem(pickUpEntry.getItemStack())).findFirst() : Optional.empty();
                if (displayEntry.isPresent()) {
                    displayEntry.get().addCount(pickUpEntry.getCount());
                    displayEntry.get().setFade(pickUpEntry.getLife());
                } else if (this.displays.size() < length) {
                    this.displays.add(new DisplayEntry(pickUpEntry.getItemStack(), pickUpEntry.getLife()));
                }
            }
            Collections.reverse(this.displays);
            this.dirty = false;
        }

        if (this.displays.isEmpty()) {
            return;
        }

        PositionPreset position = ConfigBuildHandler.DISPLAY_CONFIG.position.get();
        boolean bottom = position.isBottom();
        int x = (int) (ConfigBuildHandler.DISPLAY_CONFIG.xOffset.get() / scale);
        int y = (int) (ConfigBuildHandler.DISPLAY_CONFIG.yOffset.get() / scale);
        int offset = position.getY(DisplayEntry.HEIGHT, scaledHeight, y);
        int totalFade = ConfigBuildHandler.GENERAL_CONFIG.move.get () ? (int) (this.displays.stream().mapToDouble(DisplayEntry::getFade).average().orElse(0.0) * this.displays.size() * DisplayEntry.HEIGHT) : 0;
        int offsetFade = offset + (bottom ? totalFade : -totalFade);
        GlStateManager.scalef(scale, scale, 1.0F);

        for (DisplayEntry entry : this.displays) {
            if (bottom) {
                if (offsetFade < offset + DisplayEntry.HEIGHT) {
                    entry.render(this.mc, position.getX(entry.getTotalWidth(this.mc), scaledWidth, x), offsetFade, MathHelper.clamp((float) (offsetFade - offset) / DisplayEntry.HEIGHT, 0.0F, 1.0F));
                }
            } else if (offsetFade > offset - DisplayEntry.HEIGHT) {
                entry.render(this.mc, position.getX(entry.getTotalWidth(this.mc), scaledWidth, x), offsetFade, MathHelper.clamp((float) (offsetFade - offset) / -DisplayEntry.HEIGHT, 0.0F, 1.0F));
            }
            offsetFade += bottom ? -DisplayEntry.HEIGHT : DisplayEntry.HEIGHT;
        }

        GlStateManager.scalef(1.0F / scale, 1.0F / scale, 1.0F);

    }

}
