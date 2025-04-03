package fuzs.pickupnotifier.client.gui.entry;

import com.google.common.collect.Lists;
import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.util.DisplayEntryRenderHelper;
import fuzs.pickupnotifier.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;

import java.util.Collections;
import java.util.List;

public abstract class DisplayEntry {
    public static final int ENTRY_HEIGHT = 18;
    private static final int TEXT_ITEM_MARGIN = 4;

    private final Rarity rarity;
    private int remainingTicks;
    private int displayAmount;
    private Component component;

    protected DisplayEntry(int displayAmount, Rarity rarity) {

        this.displayAmount = displayAmount;
        this.rarity = rarity;
        this.resetEntry();
    }

    private static MutableComponent wrapInBrackets(Component toWrap) {

        return Component.literal("(").append(toWrap).append(")");
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

    protected abstract int getInventoryCount(Inventory inventory);

    public Component getTextComponent(Player player) {

        if (this.component == null) {

            int displayAmount = PickUpNotifier.CONFIG.get(ClientConfig.class).display.displayAmount.isText() ?
                    this.getDisplayAmount() : 0;
            int inventoryCount = PickUpNotifier.CONFIG.get(ClientConfig.class).display.inventoryCount ?
                    this.getInventoryCount(player.getInventory()) : 0;
            this.component = Component.empty()
                    .append(this.createTextComponent(this.getEntryName(),
                            displayAmount,
                            inventoryCount,
                            PickUpNotifier.CONFIG.get(ClientConfig.class).display.position.mirrored()))
                    .setStyle(this.getComponentStyle());
        }

        return this.component;
    }

    private Component createTextComponent(Component name, int displayAmount, int inventoryCount, boolean reverse) {

        List<Component> components = Lists.newArrayList();

        if (inventoryCount > 0) {

            components.add(wrapInBrackets(Component.literal(String.valueOf(inventoryCount))));
        }

        if (PickUpNotifier.CONFIG.get(ClientConfig.class).display.displayItemName) {

            components.add(name);
        }

        if (displayAmount > 1 ||
                displayAmount == 1 && PickUpNotifier.CONFIG.get(ClientConfig.class).display.displaySingleCount) {

            components.add(Component.literal(reverse ? displayAmount + "x" : "x" + displayAmount));
        }

        if (reverse) {

            Collections.reverse(components);
        }

        return components.stream()
                .reduce(((component1, component2) -> Component.empty()
                        .append(component1)
                        .append(" ")
                        .append(component2)))
                .orElse(Component.empty());
    }

    private Style getComponentStyle() {

        if (!PickUpNotifier.CONFIG.get(ClientConfig.class).display.ignoreRarity && this.rarity != Rarity.COMMON) {

            return Style.EMPTY.withColor(this.rarity.color());
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

    public int getEntryWidth(Minecraft minecraft) {

        int textWidth = minecraft.font.width(this.getTextComponent(minecraft.player));
        return PickUpNotifier.CONFIG.get(ClientConfig.class).display.drawSprite ?
                textWidth + (textWidth == 0 ? 0 : TEXT_ITEM_MARGIN) + 16 : textWidth;
    }

    public void render(Minecraft minecraft, GuiGraphics guiGraphics, int posX, int posY, float alpha, float scale) {

        boolean mirrorPosition = PickUpNotifier.CONFIG.get(ClientConfig.class).display.position.mirrored();
        boolean withSprite = PickUpNotifier.CONFIG.get(ClientConfig.class).display.drawSprite;
        int textStartX = mirrorPosition || !withSprite ? posX : posX + 16 + TEXT_ITEM_MARGIN;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1.0F);

        this.renderBg(minecraft, guiGraphics, posX, posY, alpha);

        float fadeTime = PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.fadeAway ? 1.0F - alpha : 1.0F;
        // prevents a bug where names would appear once at the end with full alpha
        if (fadeTime * 255.0F >= 5.0F) {

            guiGraphics.drawString(minecraft.font,
                    this.getTextComponent(minecraft.player),
                    textStartX,
                    posY + 4,
                    ARGB.white(fadeTime),
                    true);
            if (withSprite) {

                int textWidth = minecraft.font.width(this.getTextComponent(minecraft.player));
                this.renderSprite(minecraft,
                        guiGraphics,
                        mirrorPosition ? posX + textWidth + (textWidth == 0 ? 0 : TEXT_ITEM_MARGIN) : posX,
                        posY,
                        scale,
                        fadeTime);
            }
        }

        guiGraphics.pose().popPose();
    }

    private void renderBg(Minecraft minecraft, GuiGraphics guiGraphics, int posX, int posY, float alpha) {

        switch (PickUpNotifier.CONFIG.get(ClientConfig.class).display.entryBackground) {

            case CHAT -> {

                int backgroundOpacity = ARGB.color(ARGB.as8BitChannel(Mth.clamp(
                        minecraft.options.textBackgroundOpacity().get().floatValue() * (1.0F - alpha), 0.0F, 1.0F)), 0);
                int endY = posY + 16;
                if (PickUpNotifier.CONFIG.get(ClientConfig.class).display.displayAmount.isSprite()) endY += 1;
                guiGraphics.fill(posX - 3, posY, posX + this.getEntryWidth(minecraft) + 5, endY, backgroundOpacity);
            }

            case TOOLTIP -> {

                DisplayEntryRenderHelper.renderTooltipBackground(guiGraphics,
                        posX,
                        posY + 3,
                        this.getEntryWidth(minecraft),
                        9,
                        ARGB.white(1.0F - alpha));
            }
        }
    }

    protected abstract void renderSprite(Minecraft minecraft, GuiGraphics guiGraphics, int posX, int posY, float scale, float fadeTime);
}
