package com.fuzs.pickupnotifier.util;

import com.fuzs.pickupnotifier.handler.ConfigBuildHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.mutable.MutableFloat;

public class DisplayEntry {

    public static final int HEIGHT = 18;
    private static final int MARGIN = 4;

    private final ItemStack stack;
    private final ITextComponent name;
    private int count;
    private MutableFloat fade;

    public DisplayEntry(ItemStack stack, MutableFloat life) {
        this.stack = stack;
        this.name = stack.getItem().getName();
        this.count = stack.getCount();
        this.fade = life;
    }

    public boolean compareItem(ItemStack stack) {
        return this.stack.getItem() == stack.getItem();
    }

    public void addCount(int i) {
        this.count += i;
    }

    private ITextComponent getNameComponent() {
        if (ConfigBuildHandler.GENERAL_CONFIG.position.get().isMirrored()) {
            return new TextComponentString(this.count + "x ").appendSibling(this.name);
        } else {
            return this.name.createCopy().appendText(" x" + this.count);
        }
    }

    public float getFade() {
        return 1.0F - Math.min(1.0F, this.fade.floatValue() / Math.min(ConfigBuildHandler.GENERAL_CONFIG.fadeTime.get(),
                ConfigBuildHandler.GENERAL_CONFIG.displayTime.get()));
    }

    public void setFade(MutableFloat life) {
        if (this.fade.compareTo(life) < 0) {
            this.fade = life;
        }
    }

    private String getNameString() {
        Style style;
        if (!ConfigBuildHandler.GENERAL_CONFIG.ignoreRarity.get() && this.stack.getRarity() != EnumRarity.COMMON) {
            style = new Style().setColor(this.stack.getRarity().color);
        } else {
            style = new Style().setColor(ConfigBuildHandler.GENERAL_CONFIG.color.get().getChatColor());
        }
        return this.getNameComponent().setStyle(style).getFormattedText();
    }

    private int getTextWidth(Minecraft mc) {
        String s = this.getNameComponent().getString();
        return mc.fontRenderer.getStringWidth(TextFormatting.getTextWithoutFormattingCodes(s));
    }

    public int getTotalWidth(Minecraft mc) {
        int length = this.getTextWidth(mc);
        return ConfigBuildHandler.GENERAL_CONFIG.showSprite.get() ? length + MARGIN + 16 : length;
    }

    public void render(Minecraft mc, int posX, int posY, float alpha) {
        boolean mirrored = ConfigBuildHandler.GENERAL_CONFIG.position.get().isMirrored();
        boolean sprite = ConfigBuildHandler.GENERAL_CONFIG.showSprite.get();
        int i = mirrored || !sprite ? posX : posX + 16 + MARGIN;
        int textWidth = this.getTextWidth(mc);
        int k = ConfigBuildHandler.GENERAL_CONFIG.fadeForce.get() || !sprite ? 255 - (int) (255 * alpha) : 255;
        if (k > 0) {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            mc.fontRenderer.drawStringWithShadow(this.getNameString(), i, posY + 3, 16777215 + (k << 24));
            GlStateManager.disableBlend();
            if (ConfigBuildHandler.GENERAL_CONFIG.showSprite.get()) {
                GlStateManager.enableDepthTest();
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.disableLighting();
                int j = mirrored ? posX + textWidth + MARGIN : posX;
                mc.getItemRenderer().renderItemAndEffectIntoGUI(this.stack, j, posY);
                GlStateManager.enableLighting();
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableDepthTest();
            }
        }
    }

}
