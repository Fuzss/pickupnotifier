package com.fuzs.pickupnotifier.handler;

import com.fuzs.pickupnotifier.asm.hook.AddEntriesHook;
import com.fuzs.pickupnotifier.util.DisplayEntry;
import com.fuzs.pickupnotifier.util.PositionPreset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DrawEntriesHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlayText(RenderGameOverlayEvent.Text evt) {

        if (!AddEntriesHook.PICK_UPS.isEmpty() && !this.mc.isGamePaused() && ConfigBuildHandler.generalConfig.displayTime != 0) {
            synchronized (AddEntriesHook.PICK_UPS) {
                AddEntriesHook.PICK_UPS.forEach(it -> it.tick(evt.getPartialTicks()));
                AddEntriesHook.PICK_UPS.removeIf(DisplayEntry::isDead);
            }
        }

        if (AddEntriesHook.PICK_UPS.isEmpty()) {
            return;
        }

        float scale = ConfigBuildHandler.displayConfig.scale / 6.0F;
        int scaledWidth = (int) (evt.getResolution().getScaledWidth() / scale);
        int scaledHeight = (int) (evt.getResolution().getScaledHeight() / scale);
        PositionPreset position = ConfigBuildHandler.displayConfig.position;
        boolean bottom = position.isBottom();
        int posX = (int) (ConfigBuildHandler.displayConfig.xOffset / scale);
        int posY = (int) (ConfigBuildHandler.displayConfig.yOffset / scale);
        int offset = position.getY(DisplayEntry.HEIGHT, scaledHeight, posY);
        boolean move = ConfigBuildHandler.generalConfig.move;
        int totalFade = move ? (int) (AddEntriesHook.PICK_UPS.stream().mapToDouble(DisplayEntry::getRelativeLife).average().orElse(0.0) * AddEntriesHook.PICK_UPS.size() * DisplayEntry.HEIGHT) : 0;
        int renderY = offset + (bottom ? totalFade : -totalFade);
        GlStateManager.scale(scale, scale, 1.0F);

        synchronized (AddEntriesHook.PICK_UPS) {
            for (DisplayEntry entry : AddEntriesHook.PICK_UPS) {
                int renderX = position.getX(entry.getTotalWidth(this.mc), scaledWidth, posX);
                if (bottom) {
                    if (renderY < offset + DisplayEntry.HEIGHT) {
                        entry.render(this.mc, renderX, renderY, move ? MathHelper.clamp((float) (renderY - offset) / DisplayEntry.HEIGHT, 0.0F, 1.0F) : entry.getRelativeLife());
                    }
                } else if (renderY > offset - DisplayEntry.HEIGHT) {
                    entry.render(this.mc, renderX, renderY, move ? MathHelper.clamp((float) (renderY - offset) / -DisplayEntry.HEIGHT, 0.0F, 1.0F) : entry.getRelativeLife());
                }
                renderY += bottom ? -DisplayEntry.HEIGHT : DisplayEntry.HEIGHT;
            }
        }

        GlStateManager.scale(1.0F / scale, 1.0F / scale, 1.0F);

    }

}