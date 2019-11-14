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

    public DisplayEntry(ItemStack stack, int count, MutableFloat life) {
        this.stack = stack;
        this.name = new TextComponentString(stack.getItem().getItemStackDisplayName(stack));
        this.count = count;
        this.fade = life;
    }

    public boolean compareItem(ItemStack item) {
        return this.stack.getItem() == item.getItem();
    }

    public void addCount(int i) {
        this.count += i;
    }

    private ITextComponent getNameComponent() {
        if (ConfigBuildHandler.generalConfig.position.isMirrored()) {
            return new TextComponentString(this.count + "x ").appendSibling(this.name);
        } else {
            return this.name.createCopy().appendText(" x" + this.count);
        }
    }

    public float getFade() {
        return 1.0F - Math.min(1.0F, this.fade.floatValue() / Math.min(ConfigBuildHandler.generalConfig.fadeTime,
                ConfigBuildHandler.generalConfig.displayTime));
    }

    public void setFade(MutableFloat life) {
        if (this.fade.compareTo(life) < 0) {
            this.fade = life;
        }
    }

    private String getNameString() {
        Style style;
        if (!ConfigBuildHandler.generalConfig.ignoreRarity && this.stack.getItem().getForgeRarity(this.stack) != EnumRarity.COMMON) {
            style = new Style().setColor(this.stack.getItem().getForgeRarity(this.stack).getColor());
        } else {
            style = new Style().setColor(ConfigBuildHandler.generalConfig.color.getChatColor());
        }
        return this.getNameComponent().setStyle(style).getFormattedText();
    }

    private int getTextWidth(Minecraft mc) {
        String s = this.getNameComponent().getUnformattedText();
        return mc.fontRenderer.getStringWidth(TextFormatting.getTextWithoutFormattingCodes(s));
    }

    public int getTotalWidth(Minecraft mc) {
        int length = this.getTextWidth(mc);
        return ConfigBuildHandler.generalConfig.showSprite ? length + MARGIN + 16 : length;
    }

    public void render(Minecraft mc, int posX, int posY) {
        boolean flag = ConfigBuildHandler.generalConfig.position.isMirrored();
        int i = flag ? posX : posX + 16 + MARGIN;
        int textWidth = this.getTextWidth(mc);
        int alpha = ConfigBuildHandler.generalConfig.fade ? 255 - (int) (255 * this.getFade()) : 255;
        if (alpha > 0) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            mc.fontRenderer.drawStringWithShadow(this.getNameString(), i, posY + 3, 16777215 + (alpha << 24));
            GlStateManager.disableBlend();
            if (ConfigBuildHandler.generalConfig.showSprite) {
                GlStateManager.enableDepth();
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.disableLighting();
                int j = flag ? posX + textWidth + MARGIN : posX;
                mc.getRenderItem().renderItemAndEffectIntoGUI(this.stack, j, posY);
                GlStateManager.enableLighting();
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableDepth();
            }
        }
    }

}
