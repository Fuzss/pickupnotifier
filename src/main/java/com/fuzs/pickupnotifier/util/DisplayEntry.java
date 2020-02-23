package com.fuzs.pickupnotifier.util;

import com.fuzs.pickupnotifier.handler.ConfigBuildHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.mutable.MutableFloat;

@SuppressWarnings("WeakerAccess")
public abstract class DisplayEntry {

    public static final int HEIGHT = 18;
    private static final int MARGIN = 4;

    private final Rarity rarity;
    protected int count;
    private final MutableFloat life;

    protected DisplayEntry(int count, Rarity rarity) {
        this.count = Math.min(count, ConfigBuildHandler.GENERAL_CONFIG.maxCount.get());
        this.rarity = rarity;
        this.life = new MutableFloat(ConfigBuildHandler.GENERAL_CONFIG.displayTime.get());
    }

    public boolean isDead() {
        return this.life.compareTo(new MutableFloat(0.0F)) < 1;
    }

    public final void tick(float f) {
        this.life.subtract(f);
    }

    public final int getCount() {
        return this.count;
    }

    public abstract boolean canMerge(DisplayEntry entry);

    public final void addCount(int i) {
        this.count = Math.min(this.count + i, ConfigBuildHandler.GENERAL_CONFIG.maxCount.get());
    }

    protected abstract ITextComponent getName();

    private ITextComponent getNameComponent() {

        ITextComponent name = this.getName().shallowCopy();
        if (this.count <= 0) {
            return name;
        }
        if (ConfigBuildHandler.DISPLAY_CONFIG.position.get().isMirrored()) {
            return new StringTextComponent(this.count + "x ").appendSibling(name);
        } else {
            return name.appendText(" x" + this.count);
        }

    }

    public final float getRelativeLife() {
        return 1.0F - Math.min(1.0F, this.getLife() / Math.min(ConfigBuildHandler.GENERAL_CONFIG.moveTime.get(),
                ConfigBuildHandler.GENERAL_CONFIG.displayTime.get()));
    }

    protected final float getLife() {
        return Math.max(0.0F, this.life.floatValue());
    }

    public final void resetLife() {
        this.life.setValue(ConfigBuildHandler.GENERAL_CONFIG.displayTime.get());
    }

    public void merge(DisplayEntry entry) {

        this.addCount(entry.getCount());
        this.resetLife();

    }

    private Style getStyle() {

        if (!ConfigBuildHandler.GENERAL_CONFIG.ignoreRarity.get() && this.rarity != Rarity.COMMON) {
            return new Style().setColor(this.rarity.color);
        } else {
            return new Style().setColor(ConfigBuildHandler.GENERAL_CONFIG.color.get().getChatColor());
        }

    }

    private String getNameString() {
        return this.getNameComponent().setStyle(this.getStyle()).getFormattedText();
    }

    private int getTextWidth(Minecraft mc) {
        String s = this.getNameComponent().getString();
        return mc.fontRenderer.getStringWidth(TextFormatting.getTextWithoutFormattingCodes(s));
    }

    public int getTotalWidth(Minecraft mc) {
        int length = this.getTextWidth(mc);
        return ConfigBuildHandler.GENERAL_CONFIG.showSprite.get() ? length + MARGIN + 16 : length;
    }

    public final void render(Minecraft mc, int posX, int posY, float alpha) {

        boolean mirrored = ConfigBuildHandler.DISPLAY_CONFIG.position.get().isMirrored();
        boolean sprite = ConfigBuildHandler.GENERAL_CONFIG.showSprite.get();
        int i = mirrored || !sprite ? posX : posX + 16 + MARGIN;
        int textWidth = this.getTextWidth(mc);
        int opacity = mc.gameSettings.func_216839_a(0);
        if (opacity != 0) {
            AbstractGui.fill(i - 2, posY + 3 - 2, i + textWidth + 2, posY + 3 + mc.fontRenderer.FONT_HEIGHT + 2, opacity);
        }

        int k = ConfigBuildHandler.GENERAL_CONFIG.fadeAway.get() ? 255 - (int) (255 * alpha) : 255;
        if (k >= 5) { // prevents a bug where names would appear once at the end with full alpha
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            mc.fontRenderer.drawStringWithShadow(this.getNameString(), i, posY + 3, 16777215 + (k << 24));
            RenderSystem.disableBlend();
            if (sprite) {
                this.renderSprite(mc, mirrored ? posX + textWidth + MARGIN : posX, posY);
            }
            RenderSystem.popMatrix();
        }

    }

    protected abstract void renderSprite(Minecraft mc, int posX, int posY);

    @SuppressWarnings("unused")
    public abstract DisplayEntry copy();

}
