package fuzs.pickupnotifier.client.gui.entry;

import fuzs.pickupnotifier.config.ConfigValueHolder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Rarity;

public abstract class DisplayEntry {
    
    protected final Minecraft mc = Minecraft.getInstance(); 

    public static final int ENTRY_HEIGHT = 18;

    private final int textItemMargin = 4;
    private final Rarity rarity;
    protected int displayCount;
    protected int remainingTicks;

    protected DisplayEntry(int displayCount, Rarity rarity) {

        this.displayCount = Math.min(displayCount, ConfigValueHolder.getGeneralConfig().maxCount);
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

        MutableComponent name = new TextComponent("").append(this.getEntryName());
        if (this.displayCount <= 0) {

            return name;
        } else if (ConfigValueHolder.getDisplayConfig().position.isMirrored()) {

            name = new TextComponent(this.displayCount + "x ").append(name);
        } else {

            name.append(" x" + this.displayCount);
        }

        return name.setStyle(this.getComponentStyle());
    }

    private Style getComponentStyle() {

        if (!ConfigValueHolder.getGeneralConfig().ignoreRarity && this.rarity != Rarity.COMMON) {

            return Style.EMPTY.withColor(this.rarity.color);
        } else {

            return Style.EMPTY.withColor(ConfigValueHolder.getGeneralConfig().textColor);
        }
    }

    public float getRemainingTicksRelative(float partialTicks) {

        float moveTime = Math.min(ConfigValueHolder.getGeneralConfig().moveTime, ConfigValueHolder.getGeneralConfig().displayTime);
        return 1.0F - Mth.clamp((this.remainingTicks - partialTicks) / moveTime, 0.0F, 1.0F);
    }

    public void setDefaultDisplayTicks() {

        this.remainingTicks = ConfigValueHolder.getGeneralConfig().displayTime;
    }

    public abstract boolean mayMergeWith(DisplayEntry other);

    public void mergeWith(DisplayEntry other) {

        this.displayCount = Math.min(this.displayCount + other.displayCount, ConfigValueHolder.getGeneralConfig().maxCount);
        this.setDefaultDisplayTicks();
    }

    public int getEntryWidth() {

        int textWidth = this.mc.font.width(this.getTextComponent());
        return ConfigValueHolder.getGeneralConfig().showSprite ? textWidth + this.textItemMargin + 16 : textWidth;
    }

    public void render(PoseStack poseStack, int posX, int posY, float alpha, float scale) {

        boolean mirrorPosition = ConfigValueHolder.getDisplayConfig().position.isMirrored();
        boolean withSprite = ConfigValueHolder.getGeneralConfig().showSprite;
        int posXSide = mirrorPosition || !withSprite ? posX : posX + 16 + this.textItemMargin;

        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0F);

        if (!this.mc.options.backgroundForChatOnly) {

            // copied from Options::getBackgroundColor
            int backgroundOpacity = (int) (this.mc.options.textBackgroundOpacity * (1.0F - alpha) * 255.0F) << 24 & -16777216;
            GuiComponent.fill(poseStack, posX - 2, posY, posX + this.getEntryWidth() + 4, posY + 16, backgroundOpacity);
        }

        int fadeTime = ConfigValueHolder.getGeneralConfig().fadeAway ? 255 - (int) (255.0F * alpha) : 255;
        // prevents a bug where names would appear once at the end with full alpha
        if (fadeTime >= 5) {

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            GuiComponent.drawString(poseStack, this.mc.font, this.getTextComponent(), posXSide, posY + 3, 16777215 + (fadeTime << 24));
            if (withSprite) {

                int textWidth = this.mc.font.width(this.getTextComponent());
                this.renderSprite(poseStack, mirrorPosition ? posX + textWidth + this.textItemMargin : posX, posY, scale);
            }

            RenderSystem.disableBlend();
        }

        poseStack.popPose();
    }

    protected abstract void renderSprite(PoseStack matrixstack, int posX, int posY, float scale);

}
