package fuzs.pickupnotifier.client.gui.entry;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.util.DisplayEntryRenderHelper;
import fuzs.pickupnotifier.config.ClientConfig;
import fuzs.puzzleslib.api.item.v2.ItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class DisplayEntry {
    public static final int ENTRY_HEIGHT = 18;
    private static final int TEXT_ITEM_MARGIN = 4;

    private final Rarity rarity;
    private int remainingTicks;
    private int displayAmount;
    private Component component;

    public DisplayEntry(int displayAmount, Rarity rarity) {
        this.displayAmount = displayAmount;
        this.rarity = rarity;
        this.resetEntry();
    }

    public int getDisplayAmount() {
        return this.displayAmount;
    }

    public boolean mayDiscard() {
        return this.remainingTicks <= 0;
    }

    public void tick() {
        if (this.remainingTicks > 0) {
            this.remainingTicks--;
        }
    }

    protected abstract Component getEntryName();

    public Component getTextComponent() {

        if (this.component == null) {

            this.component = Component.empty()
                    .append(this.createTextComponent(PickUpNotifier.CONFIG.get(ClientConfig.class).display.position.mirrored()))
                    .setStyle(this.getComponentStyle());
        }

        return this.component;
    }

    private Component createTextComponent(boolean reverse) {

        List<Component> components = new ArrayList<>();

        this.appendTextComponents(components, reverse);

        if (reverse) {

            Collections.reverse(components);
        }

        return components.stream()
                .reduce(((component1, component2) -> Component.empty()
                        .append(component1)
                        .append(CommonComponents.SPACE)
                        .append(component2)))
                .orElse(Component.empty());
    }

    @MustBeInvokedByOverriders
    protected void appendTextComponents(List<Component> components, boolean reverse) {
        if (PickUpNotifier.CONFIG.get(ClientConfig.class).display.displayItemName) {

            components.add(this.getEntryName());
        }

        int displayAmount =
                PickUpNotifier.CONFIG.get(ClientConfig.class).display.displayAmount.isText() ? this.getDisplayAmount() :
                        0;
        if (displayAmount > 1
                || displayAmount == 1 && PickUpNotifier.CONFIG.get(ClientConfig.class).display.displaySingleCount) {

            components.add(Component.literal(reverse ? displayAmount + "x" : "x" + displayAmount));
        }
    }

    private Style getComponentStyle() {

        if (!PickUpNotifier.CONFIG.get(ClientConfig.class).display.ignoreRarity && this.rarity != Rarity.COMMON) {

            return ItemHelper.getRarityStyle(this.rarity);
        } else {

            return Style.EMPTY.withColor(PickUpNotifier.CONFIG.get(ClientConfig.class).display.textColor);
        }
    }

    public float getRemainingTicksRelative(float partialTicks) {

        float moveTime = Math.min(PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.moveTime,
                PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.displayTime);
        return 1.0F - Mth.clamp((this.remainingTicks - partialTicks) / moveTime, 0.0F, 1.0F);
    }

    public void resetEntry() {

        this.remainingTicks = PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.displayTime;
        this.component = null;
    }

    public abstract boolean mayMergeWith(DisplayEntry other, boolean excludeNamed);

    public void mergeWith(DisplayEntry other) {

        this.displayAmount = this.displayAmount + other.displayAmount;
        this.resetEntry();
    }

    public int getEntryWidth(Font font) {

        int textWidth = font.width(this.getTextComponent());
        return PickUpNotifier.CONFIG.get(ClientConfig.class).display.drawSprite ?
                textWidth + (textWidth == 0 ? 0 : TEXT_ITEM_MARGIN) + 16 : textWidth;
    }

    public void render(GuiGraphics guiGraphics, Font font, int posX, int posY, float alpha) {

        float scale = PickUpNotifier.CONFIG.get(ClientConfig.class).display.getScale();
        boolean mirrorPosition = PickUpNotifier.CONFIG.get(ClientConfig.class).display.position.mirrored();
        boolean withSprite = PickUpNotifier.CONFIG.get(ClientConfig.class).display.drawSprite;
        int textStartX = mirrorPosition || !withSprite ? posX : posX + 16 + TEXT_ITEM_MARGIN;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(scale, scale);

        this.renderBg(guiGraphics, font, posX, posY, alpha);

        float fadeTime = PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.fadeAway ? 1.0F - alpha : 1.0F;
        guiGraphics.drawString(font, this.getTextComponent(), textStartX, posY + 4, ARGB.white(fadeTime), true);
        if (withSprite) {

            int textWidth = font.width(this.getTextComponent());
            this.renderSprite(guiGraphics,
                    font,
                    mirrorPosition ? posX + textWidth + (textWidth == 0 ? 0 : TEXT_ITEM_MARGIN) : posX,
                    posY,
                    fadeTime);
        }

        guiGraphics.pose().popMatrix();
    }

    private void renderBg(GuiGraphics guiGraphics, Font font, int posX, int posY, float alpha) {

        switch (PickUpNotifier.CONFIG.get(ClientConfig.class).display.entryBackground) {

            case CHAT -> {

                int backgroundOpacity = ARGB.color(ARGB.as8BitChannel(Mth.clamp(
                        Minecraft.getInstance().options.textBackgroundOpacity().get().floatValue() * (1.0F - alpha),
                        0.0F,
                        1.0F)), 0);
                int endY = posY + 16;
                if (PickUpNotifier.CONFIG.get(ClientConfig.class).display.displayAmount.isSprite()) {
                    endY += 1;
                }

                guiGraphics.fill(posX - 3, posY, posX + this.getEntryWidth(font) + 5, endY, backgroundOpacity);
            }

            case TOOLTIP -> {

                DisplayEntryRenderHelper.renderTooltipBackground(guiGraphics,
                        posX,
                        posY + 3,
                        this.getEntryWidth(font),
                        9,
                        ARGB.white(1.0F - alpha));
            }
        }
    }

    protected abstract void renderSprite(GuiGraphics guiGraphics, Font font, int posX, int posY, float fadeTime);
}
