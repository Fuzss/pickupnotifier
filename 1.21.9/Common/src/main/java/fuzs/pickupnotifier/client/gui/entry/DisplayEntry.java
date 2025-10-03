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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class DisplayEntry<T> {
    public static final int ELEMENT_HEIGHT = 18;
    private static final int TEXT_ITEM_MARGIN = 4;

    protected final T item;
    private final int displayAmount;
    private final Component component;
    private int remainingTicks = PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.displayTime;

    public DisplayEntry(T item, int displayAmount, Rarity rarity) {
        this.item = item;
        this.displayAmount = displayAmount;
        this.component = Component.empty().append(this.createTextComponent()).setStyle(this.getComponentStyle(rarity));
    }

    public Object getKey() {
        return this;
    }

    protected abstract Component getEntryName(T item);

    public int getDisplayAmount() {
        return this.displayAmount;
    }

    public boolean mayDiscard() {
        return this.remainingTicks <= 0;
    }

    @MustBeInvokedByOverriders
    public void tick() {
        if (this.remainingTicks > 0) {
            this.remainingTicks--;
        }
    }

    protected final Component createTextComponent() {
        boolean reverse = PickUpNotifier.CONFIG.get(ClientConfig.class).display.position.isRight();
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
            components.add(this.getEntryName(this.item));
        }

        if (PickUpNotifier.CONFIG.get(ClientConfig.class).display.displayAmount.isText()) {
            if (this.displayAmount > 1 || this.displayAmount == 1
                    && PickUpNotifier.CONFIG.get(ClientConfig.class).display.displaySingleCount) {
                components.add(Component.literal(reverse ? this.displayAmount + "x" : "x" + this.displayAmount));
            }
        }
    }

    private Style getComponentStyle(Rarity rarity) {
        if (!PickUpNotifier.CONFIG.get(ClientConfig.class).display.ignoreRarity && rarity != Rarity.COMMON) {
            return ItemHelper.getRarityStyle(rarity);
        } else {
            return Style.EMPTY.withColor(PickUpNotifier.CONFIG.get(ClientConfig.class).display.textColor);
        }
    }

    public static double getRelativeRemainingTicks(Collection<DisplayEntry<?>> values, float partialTick) {
        return values.stream()
                .mapToDouble((DisplayEntry<?> displayEntry) -> 1.0
                        - displayEntry.getRelativeRemainingTicks(partialTick))
                .average()
                .orElse(0.0) * values.size();
    }

    public final float getRelativeRemainingTicks(float partialTick) {
        float moveTime = PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.getMoveTime();
        return Mth.clamp((this.remainingTicks - partialTick) / moveTime, 0.0F, 1.0F);
    }

    public abstract DisplayEntry<?> mergeWith(DisplayEntry<?> otherDisplayEntry);

    public int getEntryWidth(Font font) {
        int textWidth = font.width(this.component);
        return PickUpNotifier.CONFIG.get(ClientConfig.class).display.drawSprite ?
                textWidth + (textWidth == 0 ? 0 : TEXT_ITEM_MARGIN) + 16 : textWidth;
    }

    public void render(GuiGraphics guiGraphics, Font font, int posX, int posY, float alpha) {
        float scale = PickUpNotifier.CONFIG.get(ClientConfig.class).display.getDisplayScale();
        boolean mirrorPosition = PickUpNotifier.CONFIG.get(ClientConfig.class).display.position.isRight();
        boolean withSprite = PickUpNotifier.CONFIG.get(ClientConfig.class).display.drawSprite;
        int textStartX = mirrorPosition || !withSprite ? posX : posX + 16 + TEXT_ITEM_MARGIN;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(scale, scale);

        this.renderBg(guiGraphics, font, posX, posY, alpha);
        guiGraphics.drawString(font, this.component, textStartX, posY + 4, ARGB.white(alpha), true);
        if (withSprite) {
            int textWidth = font.width(this.component);
            this.renderSprite(guiGraphics,
                    font,
                    mirrorPosition ? posX + textWidth + (textWidth == 0 ? 0 : TEXT_ITEM_MARGIN) : posX,
                    posY,
                    alpha);
        }

        guiGraphics.pose().popMatrix();
    }

    private void renderBg(GuiGraphics guiGraphics, Font font, int posX, int posY, float alpha) {
        switch (PickUpNotifier.CONFIG.get(ClientConfig.class).display.entryBackground) {
            case CHAT -> {
                int backgroundOpacity = ARGB.color(ARGB.as8BitChannel(Mth.clamp(
                        Minecraft.getInstance().options.textBackgroundOpacity().get().floatValue() * alpha,
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
                        ARGB.white(alpha));
            }
        }
    }

    protected abstract void renderSprite(GuiGraphics guiGraphics, Font font, int posX, int posY, float alpha);

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}
