package fuzs.pickupnotifier.client.gui.entry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;

public abstract class DisplayEntry {
    public static final int ENTRY_HEIGHT = 18;
    private static final int TEXT_ITEM_MARGIN = 4;
    private static final DisplayEntryTextFactory RIGHT = (name, displayAmount, inventoryCount) -> {

        MutableComponent component;

        if (displayAmount > 1 || displayAmount == 1 && PickUpNotifier.CONFIG.get(ClientConfig.class).display.displaySingleCount) {

            component = Component.literal(displayAmount + "x ");
        } else {

            component = Component.empty();
        }

        component.append(name);

        if (inventoryCount > 0) {

            component.append(" ").append(wrapInBrackets(Component.literal(String.valueOf(inventoryCount))));
        }

        return component;
    };
    private static final DisplayEntryTextFactory LEFT = (name, displayAmount, inventoryCount) -> {

        MutableComponent component;

        if (inventoryCount > 0) {

            component = wrapInBrackets(Component.literal(String.valueOf(inventoryCount))).append(" ");
        } else {

            component = Component.empty();
        }

        component.append(name);

        if (displayAmount > 1 || displayAmount == 1 && PickUpNotifier.CONFIG.get(ClientConfig.class).display.displaySingleCount) {

            component.append(Component.literal(" x" + displayAmount));
        }

        return component;
    };

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

    public int getRemainingTicks() {

        return this.remainingTicks;
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

            DisplayEntryTextFactory factory = PickUpNotifier.CONFIG.get(ClientConfig.class).display.position.mirrored() ? RIGHT : LEFT;
            int displayAmount = PickUpNotifier.CONFIG.get(ClientConfig.class).display.displayAmount.text() ? this.getDisplayAmount() : 0;
            int inventoryCount = PickUpNotifier.CONFIG.get(ClientConfig.class).display.inventoryCount ? this.getInventoryCount(player.getInventory()) : 0;
            this.component = factory.create(this.getEntryName(), displayAmount, inventoryCount).setStyle(this.getComponentStyle());
        }

        return this.component;
    }

    private Style getComponentStyle() {

        if (!PickUpNotifier.CONFIG.get(ClientConfig.class).display.ignoreRarity && this.rarity != Rarity.COMMON) {

            return Style.EMPTY.withColor(this.rarity.color);
        } else {

            return Style.EMPTY.withColor(PickUpNotifier.CONFIG.get(ClientConfig.class).display.textColor);
        }
    }

    public float getRemainingTicksRelative(float partialTicks) {

        float moveTime = Math.min(PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.moveTime, PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.displayTime);
        return 1.0F - Mth.clamp((this.remainingTicks - partialTicks) / moveTime, 0.0F, 1.0F);
    }

    public void resetEntry() {

        this.remainingTicks = PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.displayTime;
        this.component = null;
    }

    public abstract boolean mayMergeWith(DisplayEntry other);

    public void mergeWith(DisplayEntry other) {

        this.displayAmount = this.displayAmount + other.displayAmount;
        this.resetEntry();
    }

    public int getEntryWidth(Minecraft minecraft) {

        int textWidth = minecraft.font.width(this.getTextComponent(minecraft.player));
        return PickUpNotifier.CONFIG.get(ClientConfig.class).display.drawSprite ? textWidth + TEXT_ITEM_MARGIN + 16 : textWidth;
    }

    public void render(Minecraft minecraft, PoseStack poseStack, int posX, int posY, float alpha, float scale) {

        boolean mirrorPosition = PickUpNotifier.CONFIG.get(ClientConfig.class).display.position.mirrored();
        boolean withSprite = PickUpNotifier.CONFIG.get(ClientConfig.class).display.drawSprite;
        int posXSide = mirrorPosition || !withSprite ? posX : posX + 16 + TEXT_ITEM_MARGIN;

        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0F);

        this.renderBg(minecraft, poseStack, posX, posY, alpha);

        int fadeTime = PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.fadeAway ? 255 - (int) (255.0F * alpha) : 255;
        // prevents a bug where names would appear once at the end with full alpha
        if (fadeTime >= 5) {

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            GuiComponent.drawString(poseStack, minecraft.font, this.getTextComponent(minecraft.player), posXSide, posY + 4, 16777215 | (fadeTime << 24));
            if (withSprite) {

                int textWidth = minecraft.font.width(this.getTextComponent(minecraft.player));
                this.renderSprite(minecraft, poseStack, mirrorPosition ? posX + textWidth + TEXT_ITEM_MARGIN : posX, posY, scale, fadeTime / 255.0F);
            }

            RenderSystem.disableBlend();
        }

        poseStack.popPose();
    }

    private void renderBg(Minecraft minecraft, PoseStack poseStack, int posX, int posY, float alpha) {

        switch (PickUpNotifier.CONFIG.get(ClientConfig.class).display.background) {

            case BLACK -> {

                int backgroundOpacity = (int) (minecraft.options.textBackgroundOpacity().get() * (1.0F - alpha) * 255.0F) << 24 & -16777216;
                GuiComponent.fill(poseStack, posX - 3, posY, posX + this.getEntryWidth(minecraft) + 5, posY + 16, backgroundOpacity);
            }

            case TOOLTIP -> {

                DisplayEntryRenderHelper.renderTooltipInternal(poseStack, posX, posY + 3, this.getEntryWidth(minecraft), 9, (int) ((1.0F - alpha) * 255.0F));
            }
        }
    }

    protected abstract void renderSprite(Minecraft minecraft, PoseStack poseStack, int posX, int posY, float scale, float fadeTime);

    @FunctionalInterface
    private interface DisplayEntryTextFactory {

        MutableComponent create(Component name, int displayAmount, int inventoryCount);
    }
}
