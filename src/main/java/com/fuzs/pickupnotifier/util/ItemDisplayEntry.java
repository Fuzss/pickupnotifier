package com.fuzs.pickupnotifier.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class ItemDisplayEntry extends DisplayEntry {

    private final ItemStack stack;

    public ItemDisplayEntry(ItemStack stack) {
        super(stack.getItem().getName(), stack.getCount(), stack.getRarity());
        this.stack = stack.copy();
    }

    @Override
    public boolean canCombine(DisplayEntry entry) {
        return entry instanceof ItemDisplayEntry && this.stack.getItem() == ((ItemDisplayEntry) entry).stack.getItem();
    }

    @Override
    protected void renderSprite(Minecraft mc, int posX, int posY) {

        RenderSystem.enableDepthTest();
        RenderSystem.disableLighting();
        mc.getItemRenderer().renderItemAndEffectIntoGUI(this.stack, posX, posY);
        RenderSystem.enableLighting();
        RenderHelper.disableStandardItemLighting();
        RenderSystem.disableDepthTest();

    }

    @Override
    public ItemDisplayEntry copy() {
        return new ItemDisplayEntry(this.stack);
    }

}
