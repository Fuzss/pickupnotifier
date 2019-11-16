package com.fuzs.pickupnotifier.handler;

import com.fuzs.pickupnotifier.util.DisplayEntry;
import com.fuzs.pickupnotifier.util.PickUpEntry;
import com.fuzs.pickupnotifier.util.PositionPreset;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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

    @SuppressWarnings({"unused", "ConstantConditions"})
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityItemPickup(EntityItemPickupEvent evt) {

        EntityItem item = evt.getItem();
        EntityPlayer player = evt.getEntityPlayer();
        // requires additional checks as it might actually not be possible for the item to be picked up
        boolean owner = item.getOwner() == null || item.lifespan - item.getAge() <= 200 || item.getOwner().equals(player.getName());
        if (owner && (player.inventory.getFirstEmptyStack() != -1 || player.inventory.getSlotFor(item.getEntityItem()) != -1)) {
            this.addPickUpEntry(item.getEntityItem());
        }

    }

    private void addPickUpEntry(ItemStack stack) {

        ResourceLocation resourcelocation = Item.REGISTRY.getNameForObject(stack.getItem());
        List<String> blacklist = Lists.newArrayList(ConfigBuildHandler.generalConfig.blacklist);
        boolean blacklisted = resourcelocation != null && (blacklist.contains(resourcelocation.toString())
                || blacklist.contains(resourcelocation.getResourceDomain()));

        if (!stack.isEmpty() && stack.getCount() > 0 && !blacklisted) {
            synchronized (this.pickups) {
                this.pickups.add(new PickUpEntry(stack.copy(), ConfigBuildHandler.generalConfig.displayTime));
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

        float scale = ConfigBuildHandler.displayConfig.scale / 6.0F;
        int scaledWidth = (int) (evt.getResolution().getScaledWidth() / scale);
        int scaledHeight = (int) (evt.getResolution().getScaledHeight() / scale);

        if (this.dirty) {
            this.displays.clear();
            int length = (int) (scaledHeight * ConfigBuildHandler.displayConfig.displayHeight / DisplayEntry.HEIGHT) - 1;
            List<PickUpEntry> pickupsCopy = Lists.newArrayList(this.pickups);
            Collections.reverse(pickupsCopy);
            for (PickUpEntry pickUpEntry : pickupsCopy) {
                Optional<DisplayEntry> displayEntry = ConfigBuildHandler.generalConfig.combineEntries ? this.displays.stream()
                        .filter(it -> it.compareItem(pickUpEntry.getItemStack())).findFirst() : Optional.empty();
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

        PositionPreset position = ConfigBuildHandler.displayConfig.position;
        boolean bottom = position.isBottom();
        int x = (int) (ConfigBuildHandler.displayConfig.xOffset / scale);
        int y = (int) (ConfigBuildHandler.displayConfig.yOffset / scale);
        int offset = position.getY(DisplayEntry.HEIGHT, scaledHeight, y);
        int totalFade = ConfigBuildHandler.generalConfig.move ? (int) (this.displays.stream().mapToDouble(DisplayEntry::getFade)
                .average().orElse(0.0) * this.displays.size() * DisplayEntry.HEIGHT) : 0;
        int offsetFade = offset + (bottom ? totalFade : -totalFade);
        GlStateManager.scale(scale, scale, 1.0F);

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

        GlStateManager.scale(1.0F / scale, 1.0F / scale, 1.0F);

    }

}
