package com.fuzs.pickupnotifier.client.gui.entry;

import com.fuzs.pickupnotifier.config.ConfigValueHolder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.*;
import org.apache.commons.lang3.mutable.MutableFloat;

@SuppressWarnings("WeakerAccess")
public abstract class DisplayEntry {
    
    protected final Minecraft mc = Minecraft.getInstance(); 

    public static final int HEIGHT = 18;
    private static final int MARGIN = 4;

    private final Rarity rarity;
    protected int count;
    private final MutableFloat life;

    protected DisplayEntry(int count, Rarity rarity) {

        this.count = Math.min(count, ConfigValueHolder.getGeneralConfig().maxCount);
        this.rarity = rarity;
        this.life = new MutableFloat(ConfigValueHolder.getGeneralConfig().displayTime);
    }

    public boolean isDead() {

        return this.life.compareTo(new MutableFloat(0.0F)) < 1;
    }

    public final void tick(float lostLife) {

        this.life.subtract(lostLife);
    }

    public final int getCount() {

        return this.count;
    }

    public abstract boolean canMerge(DisplayEntry entry);

    public final void addCount(int count) {

        this.count = Math.min(this.count + count, ConfigValueHolder.getGeneralConfig().maxCount);
    }

    protected abstract ITextComponent getName();

    private IFormattableTextComponent getFormattedName() {

        IFormattableTextComponent name = new StringTextComponent("").append(this.getName());
        if (this.count <= 0) {

            return name;
        } else if (ConfigValueHolder.getDisplayConfig().position.isMirrored()) {

            name = new StringTextComponent(this.count + "x ").append(name);
        } else {

            name.appendString(" x" + this.count);
        }

        return name.setStyle(this.getStyle());
    }

    public final float getRelativeLife() {

        return 1.0F - Math.min(1.0F, this.getLife() / Math.min(ConfigValueHolder.getGeneralConfig().moveTime,
                ConfigValueHolder.getGeneralConfig().displayTime));
    }

    protected final float getLife() {

        return Math.max(0.0F, this.life.floatValue());
    }

    public final void resetLife() {

        this.life.setValue(ConfigValueHolder.getGeneralConfig().displayTime);
    }

    public void merge(DisplayEntry entry) {

        this.addCount(entry.getCount());
        this.resetLife();
    }

    private Style getStyle() {

        if (!ConfigValueHolder.getGeneralConfig().ignoreRarity && this.rarity != Rarity.COMMON) {

            return Style.EMPTY.setFormatting(this.rarity.color);
        } else {

            return Style.EMPTY.setFormatting(ConfigValueHolder.getGeneralConfig().textColor);
        }
    }

    public int getTotalWidth() {

        int width = this.mc.fontRenderer.func_238414_a_(this.getFormattedName());
        return ConfigValueHolder.getGeneralConfig().showSprite ? width + MARGIN + 16 : width;
    }

    @SuppressWarnings("deprecation")
    public final void render(MatrixStack matrixstack, int posX, int posY, float alpha) {

        boolean mirrored = ConfigValueHolder.getDisplayConfig().position.isMirrored();
        boolean sprite = ConfigValueHolder.getGeneralConfig().showSprite;
        int i = mirrored || !sprite ? posX : posX + 16 + MARGIN;

        int textWidth = this.mc.fontRenderer.func_238414_a_(this.getFormattedName());
        int opacity = this.mc.gameSettings.getChatBackgroundColor(0);
        if (opacity != 0) {

            AbstractGui.fill(matrixstack, i - 2, posY + 3 - 2, i + textWidth + 2, posY + 3 + this.mc.fontRenderer.FONT_HEIGHT + 2, opacity);
        }

        int fadeTime = ConfigValueHolder.getGeneralConfig().fadeAway ? 255 - (int) (255 * alpha) : 255;
        // prevents a bug where names would appear once at the end with full alpha
        if (fadeTime >= 5) {

            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            this.mc.fontRenderer.func_238407_a_(matrixstack, this.getFormattedName(), i, posY + 3, 16777215 + (fadeTime << 24));
            RenderSystem.disableBlend();
            if (sprite) {

                this.renderSprite(matrixstack, mirrored ? posX + textWidth + MARGIN : posX, posY);
            }

            RenderSystem.popMatrix();
        }
    }

    protected abstract void renderSprite(MatrixStack matrixstack, int posX, int posY);

}
