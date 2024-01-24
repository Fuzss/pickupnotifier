package fuzs.pickupnotifier.client.util;

import com.google.common.collect.ImmutableSortedMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.config.ClientConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.NavigableMap;

public class DisplayEntryRenderHelper {
    private static final NavigableMap<Integer, Character> MAP = ImmutableSortedMap.<Integer, Character>naturalOrder().put(1_000, 'K').put(1_000_000, 'M').put(1_000_000_000, 'B').build();

    private static MutableComponent shortenValue(int value) {

        Map.Entry<Integer, Character> entry = MAP.floorEntry(value);

        if (entry == null) {

            return Component.literal(String.valueOf(value));
        }

        return Component.literal(String.valueOf(value / entry.getKey()) + entry.getValue());
    }

    public static void renderGuiItemDecorations(GuiGraphics guiGraphics, Font font, int count, int xPosition, int yPosition) {

        if (count <= 1 && !PickUpNotifier.CONFIG.get(ClientConfig.class).display.displaySingleCount) return;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0, 0.0, 200.0F);

        Component component = shortenValue(count);
        float scale = Math.min(1.0F, 16.0F / font.width(component));
        guiGraphics.pose().scale(scale, scale, 1.0F);

        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        float posX = (xPosition + 17) / scale - font.width(component);
        float posY = (yPosition + font.lineHeight * 2) / scale - font.lineHeight;
        font.drawInBatch(component, posX, posY, 16777215, true, guiGraphics.pose().last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        bufferSource.endBatch();
        guiGraphics.pose().popPose();
    }

    public static void renderTooltipInternal(GuiGraphics guiGraphics, int posX, int posY, int width, int height, int alpha) {

        guiGraphics.pose().pushPose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        int color = applyAlphaComponent(-267386864, alpha);
        int blitOffset = 0;
        fillGradient(matrix4f, bufferBuilder, posX - 3, posY - 4, posX + width + 3, posY - 3, blitOffset, color, color);
        fillGradient(matrix4f, bufferBuilder, posX - 3, posY + height + 3, posX + width + 3, posY + height + 4, blitOffset, color, color);
        fillGradient(matrix4f, bufferBuilder, posX - 3, posY - 3, posX + width + 3, posY + height + 3, blitOffset, color, color);
        fillGradient(matrix4f, bufferBuilder, posX - 4, posY - 3, posX - 3, posY + height + 3, blitOffset, color, color);
        fillGradient(matrix4f, bufferBuilder, posX + width + 3, posY - 3, posX + width + 4, posY + height + 3, blitOffset, color, color);
        int colorA = applyAlphaComponent(1347420415, alpha);
        int colorB = applyAlphaComponent(1344798847, alpha);
        fillGradient(matrix4f, bufferBuilder, posX - 3, posY - 3 + 1, posX - 3 + 1, posY + height + 3 - 1, blitOffset, colorA, colorB);
        fillGradient(matrix4f, bufferBuilder, posX + width + 2, posY - 3 + 1, posX + width + 3, posY + height + 3 - 1, blitOffset, colorA, colorB);
        fillGradient(matrix4f, bufferBuilder, posX - 3, posY - 3, posX + width + 3, posY - 3 + 1, blitOffset, colorA, colorA);
        fillGradient(matrix4f, bufferBuilder, posX - 3, posY + height + 2, posX + width + 3, posY + height + 3, blitOffset, colorB, colorB);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
        guiGraphics.pose().popPose();
    }

    private static int applyAlphaComponent(int color, int alpha) {

        return color & 0xFFFFFF | Math.min(color >> 24 & 0xFF, alpha) << 24;
    }

    private static void fillGradient(Matrix4f matrix, BufferBuilder builder, int x1, int y1, int x2, int y2, int blitOffset, int colorA, int colorB) {
        float f = (float)(colorA >> 24 & 0xFF) / 255.0F;
        float g = (float)(colorA >> 16 & 0xFF) / 255.0F;
        float h = (float)(colorA >> 8 & 0xFF) / 255.0F;
        float i = (float)(colorA & 0xFF) / 255.0F;
        float j = (float)(colorB >> 24 & 0xFF) / 255.0F;
        float k = (float)(colorB >> 16 & 0xFF) / 255.0F;
        float l = (float)(colorB >> 8 & 0xFF) / 255.0F;
        float m = (float)(colorB & 0xFF) / 255.0F;
        builder.vertex(matrix, (float)x2, (float)y1, (float)blitOffset).color(g, h, i, f).endVertex();
        builder.vertex(matrix, (float)x1, (float)y1, (float)blitOffset).color(g, h, i, f).endVertex();
        builder.vertex(matrix, (float)x1, (float)y2, (float)blitOffset).color(k, l, m, j).endVertex();
        builder.vertex(matrix, (float)x2, (float)y2, (float)blitOffset).color(k, l, m, j).endVertex();
    }
}
