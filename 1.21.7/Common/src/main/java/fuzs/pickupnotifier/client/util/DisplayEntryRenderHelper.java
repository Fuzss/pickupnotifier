package fuzs.pickupnotifier.client.util;

import com.google.common.collect.ImmutableSortedMap;
import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.config.ClientConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.NavigableMap;

public class DisplayEntryRenderHelper {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("tooltip/background");
    private static final ResourceLocation FRAME_SPRITE = ResourceLocation.withDefaultNamespace("tooltip/frame");
    private static final NavigableMap<Integer, Character> MAP = ImmutableSortedMap.<Integer, Character>naturalOrder()
            .put(1_000, 'K')
            .put(1_000_000, 'M')
            .put(1_000_000_000, 'B')
            .build();

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

        float posX = (xPosition + 17) / scale - font.width(component);
        float posY = (yPosition + font.lineHeight * 2) / scale - font.lineHeight;
        guiGraphics.drawSpecial((MultiBufferSource bufferSource) -> {
            font.drawInBatch(component,
                    posX,
                    posY,
                    -1,
                    true,
                    guiGraphics.pose().last().pose(),
                    bufferSource,
                    Font.DisplayMode.NORMAL,
                    0,
                    0XF000F0);
        });
        guiGraphics.pose().popPose();
    }

    /**
     * @see net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil#renderTooltipBackground(GuiGraphics,
     *         int, int, int, int, int, ResourceLocation)
     */
    public static void renderTooltipBackground(GuiGraphics guiGraphics, int x, int y, int width, int height, int color) {
        int i = x - 3 - 9;
        int j = y - 3 - 9;
        int k = width + 3 + 3 + 18;
        int l = height + 3 + 3 + 18;
        guiGraphics.pose().pushPose();
        guiGraphics.blitSprite(RenderType::guiTextured, BACKGROUND_SPRITE, i, j, k, l, color);
        guiGraphics.blitSprite(RenderType::guiTextured, FRAME_SPRITE, i, j, k, l, color);
        guiGraphics.pose().popPose();
    }
}
