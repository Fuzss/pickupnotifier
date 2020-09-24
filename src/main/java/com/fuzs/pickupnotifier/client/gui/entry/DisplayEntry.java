package com.fuzs.pickupnotifier.client.gui.entry;

import com.fuzs.pickupnotifier.config.ConfigValueHolder;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public abstract class DisplayEntry {

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

    private ITextComponent getNameComponent() {

        ITextComponent name = this.getName().shallowCopy();
        if (this.count <= 0) {

            return name;
        }

        if (ConfigValueHolder.getDisplayConfig().position.isMirrored()) {

            return new StringTextComponent(this.count + "x ").appendSibling(name);
        } else {

            return name.appendText(" x" + this.count);
        }
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

            return new Style().setColor(this.rarity.color);
        } else {

            return new Style().setColor(ConfigValueHolder.getGeneralConfig().textColor);
        }
    }

    private String getNameString() {

        return this.getNameComponent().setStyle(this.getStyle()).getFormattedText();
    }

    private int getTextWidth(FontRenderer fontRenderer) {

        String name = this.getNameComponent().getString();
        return fontRenderer.getStringWidth(Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(name)));
    }

    public int getTotalWidth(FontRenderer fontRenderer) {

        int length = this.getTextWidth(fontRenderer);
        return ConfigValueHolder.getGeneralConfig().showSprite ? length + MARGIN + 16 : length;
    }

    public final void render(Minecraft mc, int posX, int posY, float alpha) {

        boolean mirrored = ConfigValueHolder.getDisplayConfig().position.isMirrored();
        boolean sprite = ConfigValueHolder.getGeneralConfig().showSprite;
        int i = mirrored || !sprite ? posX : posX + 16 + MARGIN;
        int textWidth = this.getTextWidth(mc.fontRenderer);
        int opacity = mc.gameSettings.func_216839_a(0);
        if (opacity != 0) {

            AbstractGui.fill(i - 2, posY + 3 - 2, i + textWidth + 2, posY + 3 + mc.fontRenderer.FONT_HEIGHT + 2, opacity);
        }

        int fadeTime = ConfigValueHolder.getGeneralConfig().fadeAway ? 255 - (int) (255 * alpha) : 255;
        // prevents a bug where names would appear once at the end with full alpha
        if (fadeTime >= 5) {

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            mc.fontRenderer.drawStringWithShadow(this.getNameString(), i, posY + 3, 16777215 + (fadeTime << 24));
            GlStateManager.disableBlend();
            if (sprite) {

                this.renderSprite(mc, mirrored ? posX + textWidth + MARGIN : posX, posY);
            }

            GlStateManager.popMatrix();
        }
    }

    protected abstract void renderSprite(Minecraft mc, int posX, int posY);

}
