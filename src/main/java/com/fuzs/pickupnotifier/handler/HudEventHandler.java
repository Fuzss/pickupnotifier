package com.fuzs.pickupnotifier.handler;

import com.fuzs.pickupnotifier.util.DisplayEntry;
import com.fuzs.pickupnotifier.util.PickUpEntry;
import com.fuzs.pickupnotifier.util.PositionPreset;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
    @SubscribeEvent
    public void onItemPickup(PlayerEvent.ItemPickupEvent evt) {

        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(evt.getStack().getItem());
        boolean blacklisted = resourcelocation != null && (ConfigBuildHandler.GENERAL_CONFIG.blacklist.get().contains(resourcelocation.toString())
                || ConfigBuildHandler.GENERAL_CONFIG.blacklist.get().contains(resourcelocation.getNamespace()));
        if (!blacklisted && evt.getStack().getCount() > 0) {
            synchronized (this.pickups) {
                this.pickups.add(new PickUpEntry(evt.getStack(), ConfigBuildHandler.GENERAL_CONFIG.displayTime.get()));
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

        float scale = ConfigBuildHandler.GENERAL_CONFIG.scale.get() / 6.0F;
        int scaledWidth = (int) (evt.getWindow().getScaledWidth() / scale);
        int scaledHeight = (int) (evt.getWindow().getScaledHeight() / scale);

        if (this.dirty) {
            this.displays.clear();
            int length = (int) (scaledHeight * ConfigBuildHandler.GENERAL_CONFIG.displayHeight.get().floatValue() / DisplayEntry.HEIGHT) - 1;
            List<PickUpEntry> pickupsCopy = Lists.newArrayList(this.pickups);
            Collections.reverse(pickupsCopy);
            for (PickUpEntry pickUpEntry : pickupsCopy) {
                Optional<DisplayEntry> displayEntry = this.displays.stream().filter(it -> it.compareItem(pickUpEntry.getItemStack())).findFirst();
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

        PositionPreset position = ConfigBuildHandler.GENERAL_CONFIG.position.get();
        boolean bottom = position.isBottom();
        int x = ConfigBuildHandler.GENERAL_CONFIG.xOffset.get();
        int y = ConfigBuildHandler.GENERAL_CONFIG.yOffset.get();
        int offset = position.getY(DisplayEntry.HEIGHT, scaledHeight, y);
        int totalFade = (int) (this.displays.stream().mapToDouble(DisplayEntry::getFade).average().orElse(0.0) * this.displays.size() * DisplayEntry.HEIGHT);
        offset += bottom ? totalFade : -totalFade;
        GlStateManager.scalef(scale, scale, 1.0F);

        for (DisplayEntry entry : this.displays) {
            entry.render(this.mc, position.getX(entry.getTotalWidth(this.mc), scaledWidth, x), offset);
            offset += bottom ? -DisplayEntry.HEIGHT : DisplayEntry.HEIGHT;
        }

        GlStateManager.scalef(1.0F / scale, 1.0F / scale, 1.0F);

    }

}
