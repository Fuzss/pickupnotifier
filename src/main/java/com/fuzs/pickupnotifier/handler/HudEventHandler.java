package com.fuzs.pickupnotifier.handler;

import com.fuzs.pickupnotifier.util.DisplayEntry;
import com.fuzs.pickupnotifier.util.PickUpEntry;
import com.fuzs.pickupnotifier.util.PositionPreset;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class HudEventHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

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
        List<String> blacklist = Lists.newArrayList(ConfigBuildHandler.generalConfig.blacklist);
        boolean blacklisted = resourcelocation != null && (blacklist.contains(resourcelocation.toString())
                || blacklist.contains(resourcelocation.getResourceDomain()));
        int count = evt.getStack().getCount();
        if (!blacklisted && count > 0) {
            synchronized (this.pickups) {
                this.pickups.add(new PickUpEntry(evt.getStack(), count, ConfigBuildHandler.generalConfig.displayTime));
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

        float scale = ConfigBuildHandler.generalConfig.scale / 6.0F;
        int scaledWidth = (int) (evt.getResolution().getScaledWidth() / scale);
        int scaledHeight = (int) (evt.getResolution().getScaledHeight() / scale);

        if (this.dirty) {
            this.displays.clear();
            int length = (int) (scaledHeight * ConfigBuildHandler.generalConfig.displayHeight / DisplayEntry.HEIGHT) - 1;
            List<PickUpEntry> pickupsCopy = Lists.newArrayList(this.pickups);
            Collections.reverse(pickupsCopy);
            for (PickUpEntry pickUpEntry : pickupsCopy) {
                Optional<DisplayEntry> displayEntry = this.displays.stream().filter(it -> it.compareItem(pickUpEntry.getItemStack())).findFirst();
                if (displayEntry.isPresent()) {
                    displayEntry.get().addCount(pickUpEntry.getCount());
                    displayEntry.get().setFade(pickUpEntry.getLife());
                } else if (this.displays.size() < length) {
                    this.displays.add(new DisplayEntry(pickUpEntry.getItemStack(), pickUpEntry.getCount(), pickUpEntry.getLife()));
                }
            }
            Collections.reverse(this.displays);
            this.dirty = false;
        }

        if (this.displays.isEmpty()) {
            return;
        }

        PositionPreset position = ConfigBuildHandler.generalConfig.position;
        boolean bottom = position.isBottom();
        int x = ConfigBuildHandler.generalConfig.xOffset;
        int y = ConfigBuildHandler.generalConfig.yOffset;
        int offset = position.getY(DisplayEntry.HEIGHT, scaledHeight, y);
        int totalFade = (int) (this.displays.stream().mapToDouble(DisplayEntry::getFade).average().orElse(0.0) * this.displays.size() * DisplayEntry.HEIGHT);
        offset += bottom ? totalFade : -totalFade;
        GlStateManager.scale(scale, scale, 1.0F);

        for (DisplayEntry entry : this.displays) {
            entry.render(this.mc, position.getX(entry.getTotalWidth(this.mc), scaledWidth, x), offset);
            offset += bottom ? -DisplayEntry.HEIGHT : DisplayEntry.HEIGHT;
        }

        GlStateManager.scale(1.0F / scale, 1.0F / scale, 1.0F);

    }

}
