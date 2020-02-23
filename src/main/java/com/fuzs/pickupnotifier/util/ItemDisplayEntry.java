package com.fuzs.pickupnotifier.util;

import com.fuzs.pickupnotifier.handler.ConfigBuildHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class ItemDisplayEntry extends DisplayEntry {

    private final ItemStack stack;

    public ItemDisplayEntry(ItemStack stack) {
        super(stack.getCount(), stack.getRarity());
        this.stack = stack;
    }

    @Override
    protected ITextComponent getName() {
        return ConfigBuildHandler.GENERAL_CONFIG.combineEntries.get() ? this.stack.getItem().getName() : this.stack.getDisplayName();
    }

    @Override
    public boolean canMerge(DisplayEntry entry) {
        return entry instanceof ItemDisplayEntry && this.stack.getItem() == ((ItemDisplayEntry) entry).stack.getItem();
    }

    @Override
    protected void renderSprite(Minecraft mc, int posX, int posY) {

        GlStateManager.enableDepthTest();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        mc.getItemRenderer().renderItemAndEffectIntoGUI(this.stack, posX, posY);
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepthTest();

    }

    @Override
    public ItemDisplayEntry copy() {
        return new ItemDisplayEntry(this.stack);
    }

}
