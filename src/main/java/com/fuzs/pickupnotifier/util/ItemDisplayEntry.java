package com.fuzs.pickupnotifier.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class ItemDisplayEntry extends DisplayEntry {

    private final ItemStack stack;

    public ItemDisplayEntry(ItemStack stack) {
        super(new TextComponentString(stack.getItem().getItemStackDisplayName(stack)), stack.getCount(), stack.getItem().getForgeRarity(stack));
        this.stack = stack.copy();
    }

    @Override
    public boolean canCombine(DisplayEntry entry) {
        return entry instanceof ItemDisplayEntry && this.stack.getItem() == ((ItemDisplayEntry) entry).stack.getItem();
    }

    @Override
    protected void renderSprite(Minecraft mc, int posX, int posY) {

        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(this.stack, posX, posY);
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();

    }

    @Override
    public ItemDisplayEntry copy() {
        return new ItemDisplayEntry(this.stack);
    }

}
