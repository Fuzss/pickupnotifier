package com.fuzs.pickupnotifier.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class ExperienceDisplayEntry extends DisplayEntry {

    private final ITextComponent name;

    private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = new ResourceLocation("textures/entity/experience_orb.png");

    public ExperienceDisplayEntry(ExperienceOrbEntity orb) {
        this(orb.getName(), orb.xpValue);
    }

    private ExperienceDisplayEntry(ITextComponent name, int count) {
        super(count, Rarity.COMMON);
        this.name = name;
    }

    @Override
    protected ITextComponent getName() {
        return this.name;
    }

    @Override
    public boolean canMerge(DisplayEntry entry) {
        return entry instanceof ExperienceDisplayEntry;
    }

    @Override
    protected void renderSprite(Minecraft mc, int posX, int posY) {

        int i = this.getTextureForCount();
        int x = i % 4 * 16;
        int y = i / 4 * 16;
        float color = this.getLife() / 4.0F;
        float r = (MathHelper.sin(color) + 1.0F) * 0.5F;
        float g = 1.0F;
        float b = (MathHelper.sin(color + 4.1887903F) + 1.0F) * 0.1F;

        mc.getTextureManager().bindTexture(EXPERIENCE_ORB_TEXTURES);
        RenderSystem.enableBlend();
        RenderSystem.color4f(r, g, b, 1.0F);
        AbstractGui.blit(posX, posY, x, y, 16, 16, 64, 64);
        RenderSystem.disableBlend();

    }

    /**
     * returns a number from 0 to 10 based on how much experience this orb is worth, used to determine the texture to use
     * taken from ExperienceOrbEntity#getTextureByXP
     */
    private int getTextureForCount() {

        if (this.count >= 2477) {
            return 10;
        } else if (this.count >= 1237) {
            return 9;
        } else if (this.count >= 617) {
            return 8;
        } else if (this.count >= 307) {
            return 7;
        } else if (this.count >= 149) {
            return 6;
        } else if (this.count >= 73) {
            return 5;
        } else if (this.count >= 37) {
            return 4;
        } else if (this.count >= 17) {
            return 3;
        } else if (this.count >= 7) {
            return 2;
        } else {
            return this.count >= 3 ? 1 : 0;
        }

    }

    @Override
    public ExperienceDisplayEntry copy() {
        return new ExperienceDisplayEntry(this.name, this.count);
    }

}
