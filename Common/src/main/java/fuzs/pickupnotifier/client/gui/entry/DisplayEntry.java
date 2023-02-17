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
import net.minecraft.world.item.Rarity;

public abstract class DisplayEntry {
    
    protected final Minecraft minecraft = Minecraft.getInstance();

    public static final int ENTRY_HEIGHT = 18;

    private final int textItemMargin = 4;
    private final Rarity rarity;
    protected int displayAmount;
    protected int remainingTicks;

    protected DisplayEntry(int amount, Rarity rarity) {

        this.displayAmount = Math.min(amount, PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.maxCount);
        this.rarity = rarity;
        this.setDefaultDisplayTicks();
    }

    public boolean readyToRemove() {

        return this.remainingTicks <= 0;
    }

    public void tick() {

        if (this.remainingTicks > 0) {

            this.remainingTicks--;
        }
    }

    protected abstract Component getEntryName();

    public MutableComponent getTextComponent() {

        MutableComponent name = Component.empty().append(this.getEntryName());
        if (this.displayAmount <= 0) {

            return name;
        } else if (PickUpNotifier.CONFIG.get(ClientConfig.class).display.position.mirrored()) {

            name = Component.literal(this.displayAmount + "x ").append(name);
        } else {

            name.append(" x" + this.displayAmount);
        }

        return name.setStyle(this.getComponentStyle());
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

    public void setDefaultDisplayTicks() {

        this.remainingTicks = PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.displayTime;
    }

    public abstract boolean mayMergeWith(DisplayEntry other);

    public void mergeWith(DisplayEntry other) {

        this.displayAmount = Math.min(this.displayAmount + other.displayAmount, PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.maxCount);
        this.setDefaultDisplayTicks();
    }

    public int getEntryWidth() {

        int textWidth = this.minecraft.font.width(this.getTextComponent());
        return PickUpNotifier.CONFIG.get(ClientConfig.class).display.showSprite ? textWidth + this.textItemMargin + 16 : textWidth;
    }

    public void render(PoseStack poseStack, int posX, int posY, float alpha, float scale) {

        boolean mirrorPosition = PickUpNotifier.CONFIG.get(ClientConfig.class).display.position.mirrored();
        boolean withSprite = PickUpNotifier.CONFIG.get(ClientConfig.class).display.showSprite;
        int posXSide = mirrorPosition || !withSprite ? posX : posX + 16 + this.textItemMargin;

        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0F);

        if (!this.minecraft.options.backgroundForChatOnly().get()) {

            // copied from Options::getBackgroundColor
            int backgroundOpacity = (int) (this.minecraft.options.textBackgroundOpacity().get() * (1.0F - alpha) * 255.0F) << 24 & -16777216;
            GuiComponent.fill(poseStack, posX - 2, posY, posX + this.getEntryWidth() + 4, posY + 16, backgroundOpacity);
        }

        int fadeTime = PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.fadeAway ? 255 - (int) (255.0F * alpha) : 255;
        // prevents a bug where names would appear once at the end with full alpha
        if (fadeTime >= 5) {

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            GuiComponent.drawString(poseStack, this.minecraft.font, this.getTextComponent(), posXSide, posY + 3, 16777215 + (fadeTime << 24));
            if (withSprite) {

                int textWidth = this.minecraft.font.width(this.getTextComponent());
                this.renderSprite(poseStack, mirrorPosition ? posX + textWidth + this.textItemMargin : posX, posY, scale);
            }

            RenderSystem.disableBlend();
        }

        poseStack.popPose();
    }

    protected abstract void renderSprite(PoseStack poseStack, int posX, int posY, float scale);
}
