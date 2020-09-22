package com.fuzs.pickupnotifier.client.gui.entry;

import com.fuzs.pickupnotifier.config.ConfigValueHolder;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
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

        return ConfigValueHolder.getGeneralConfig().combineEntries ? this.stack.getItem().getName() : this.stack.getDisplayName();
    }

    @Override
    public boolean canMerge(DisplayEntry entry) {

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

}
