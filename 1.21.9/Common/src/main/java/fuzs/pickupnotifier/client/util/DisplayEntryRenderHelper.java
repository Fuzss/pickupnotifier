package fuzs.pickupnotifier.client.util;

import com.google.common.collect.ImmutableSortedMap;
import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.config.ClientConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;

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

    private static float itemStackAlpha = 1.0F;

    public static void renderItem(GuiGraphics guiGraphics, ItemStack itemStack, int x, int y, float alpha) {
        itemStackAlpha = alpha;
        guiGraphics.renderItem(itemStack, x, y);
        itemStackAlpha = 1.0F;
    }

    public static float getItemStackAlpha() {
        return itemStackAlpha;
    }

    private static MutableComponent shortenValue(int value) {

        Map.Entry<Integer, Character> entry = MAP.floorEntry(value);

        if (entry == null) {

            return Component.literal(String.valueOf(value));
        }

        return Component.literal(String.valueOf(value / entry.getKey()) + entry.getValue());
    }

    public static void renderGuiItemDecorations(GuiGraphics guiGraphics, Font font, int count, int xPosition, int yPosition, float alpha) {

        if (count <= 1 && !PickUpNotifier.CONFIG.get(ClientConfig.class).display.displaySingleCount) return;

        guiGraphics.pose().pushMatrix();

        Component component = shortenValue(count);
        float scale = Math.min(1.0F, 16.0F / font.width(component));
        guiGraphics.pose().scale(scale, scale);

        float posX = (xPosition + 17) / scale - font.width(component);
        float posY = (yPosition + font.lineHeight * 2) / scale - font.lineHeight;
        guiGraphics.drawString(font, component, (int) posX, (int) posY, ARGB.color(alpha, -1), true);

        guiGraphics.pose().popMatrix();
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
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, i, j, k, l, color);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, FRAME_SPRITE, i, j, k, l, color);
    }
}
