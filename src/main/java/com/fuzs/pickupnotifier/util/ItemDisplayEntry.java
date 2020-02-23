package com.fuzs.pickupnotifier.util;

import com.fuzs.pickupnotifier.handler.ConfigBuildHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ItemDisplayEntry extends DisplayEntry {

    private final ItemStack stack;

    @SuppressWarnings("deprecation")
    public ItemDisplayEntry(ItemStack stack) {
        super(stack.getCount(), stack.getRarity());
        this.stack = stack;
    }

    @Override
    protected ITextComponent getName() {
        return ConfigBuildHandler.generalConfig.combineEntries ? new TextComponentString(this.stack.getItem().getItemStackDisplayName(stack)) : new TextComponentString(this.stack.getDisplayName());
    }

    @Override
    public boolean canMerge(DisplayEntry entry) {
        return super.canMerge(entry) && entry instanceof ItemDisplayEntry && this.stack.getItem() == ((ItemDisplayEntry) entry).stack.getItem()
                && ((ItemDisplayEntry) entry).stack.getItem().getItemStackDisplayName(((ItemDisplayEntry) entry).stack).equals(this.getName().getUnformattedComponentText());
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
