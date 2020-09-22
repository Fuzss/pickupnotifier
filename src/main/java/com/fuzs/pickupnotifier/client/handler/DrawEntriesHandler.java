package com.fuzs.pickupnotifier.client.handler;

import com.fuzs.pickupnotifier.client.gui.PositionPreset;
import com.fuzs.pickupnotifier.client.gui.entry.DisplayEntry;
import com.fuzs.pickupnotifier.client.util.PickUpCollector;
import com.fuzs.pickupnotifier.config.ConfigValueHolder;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DrawEntriesHandler {

    public static final PickUpCollector PICK_UPS = new PickUpCollector();

    private final Minecraft mc = Minecraft.getInstance();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent evt) {

        if (evt.phase != TickEvent.Phase.END || this.mc.isGamePaused()) {

            return;
        }

        if (!this.mc.isGamePaused() && ConfigValueHolder.getGeneralConfig().displayTime != 0) {

            PICK_UPS.tick(evt.renderTickTime);
        }
    }

    @SuppressWarnings({"unused", "deprecation"})
    @SubscribeEvent
    public void onRenderGameOverlayText(RenderGameOverlayEvent.Text evt) {

        if (PICK_UPS.isEmpty()) {

            return;
        }

        float scale = ConfigValueHolder.getDisplayConfig().scale / 6.0F;
        int scaledWidth = (int) (evt.getWindow().getScaledWidth() / scale);
        int scaledHeight = (int) (evt.getWindow().getScaledHeight() / scale);
        PositionPreset position = ConfigValueHolder.getDisplayConfig().position;
        boolean bottom = position.isBottom();
        int posX = (int) (ConfigValueHolder.getDisplayConfig().xOffset / scale);
        int posY = (int) (ConfigValueHolder.getDisplayConfig().yOffset / scale);
        int offset = position.getY(DisplayEntry.HEIGHT, scaledHeight, posY);
        boolean move = ConfigValueHolder.getGeneralConfig().move;
        int totalFade = move ? (int) (PICK_UPS.getTotalFade() * DisplayEntry.HEIGHT) : 0;
        int renderY = offset + (bottom ? totalFade : -totalFade);

        RenderSystem.scalef(scale, scale, 1.0F);
        for (DisplayEntry entry : PICK_UPS) {

            int renderX = position.getX(entry.getTotalWidth(), scaledWidth, posX);
            if (bottom) {

                if (renderY < offset + DisplayEntry.HEIGHT) {

                    entry.render(evt.getMatrixStack(), renderX, renderY, move ? MathHelper.clamp((float) (renderY - offset) / DisplayEntry.HEIGHT, 0.0F, 1.0F) : entry.getRelativeLife());
                }
            } else if (renderY > offset - DisplayEntry.HEIGHT) {

                entry.render(evt.getMatrixStack(), renderX, renderY, move ? MathHelper.clamp((float) (renderY - offset) / -DisplayEntry.HEIGHT, 0.0F, 1.0F) : entry.getRelativeLife());
            }

            renderY += bottom ? -DisplayEntry.HEIGHT : DisplayEntry.HEIGHT;
        }

        RenderSystem.scalef(1.0F / scale, 1.0F / scale, 1.0F);
    }

}
