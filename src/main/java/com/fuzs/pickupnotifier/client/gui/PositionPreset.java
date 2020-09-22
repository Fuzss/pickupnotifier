package com.fuzs.pickupnotifier.client.gui;

import net.minecraft.potion.EffectInstance;

import java.util.Collection;

@SuppressWarnings("unused")
public enum PositionPreset {

    TOP_LEFT("top_left", 0, 0, false),
    TOP_RIGHT("top_right", 1, 0, true),
    BOTTOM_LEFT("bottom_left", 0, 1, false),
    BOTTOM_RIGHT("bottom_right", 1, 1, false);

    private final int posX;
    private final int posY;
    private final boolean shift;

    PositionPreset(String name, int posX, int posY, boolean potionShift) {

        this.posX = posX;
        this.posY = posY;
        this.shift = potionShift;
    }

    public boolean isMirrored() {

        return this.posX == 1;
    }

    public boolean isBottom() {

        return this.posY == 1;
    }

    public int getX(int textureWidth, int scaledWidth, int offset) {

        return Math.abs((scaledWidth - textureWidth) * this.posX - offset);
    }

    public int getY(int textureHeight, int scaledHeight, int offset) {

        return Math.abs((scaledHeight - textureHeight) * this.posY - offset);
    }

    public float getRotation(float rotation) {

        // inverts a value depending on the display side
        return -(rotation - rotation * 2 * this.posX);
    }

    public int getPotionShift(Collection<EffectInstance> activeEffects) {

        if (!this.shift) {

            return 0;
        }

        boolean renderInHUD = activeEffects.stream().anyMatch(effect -> effect.getPotion().shouldRenderHUD(effect));
        boolean showsParticles = activeEffects.stream().anyMatch(EffectInstance::doesShowParticles);
        if (!activeEffects.isEmpty() && renderInHUD && showsParticles) {

            return activeEffects.stream().anyMatch(effect -> !effect.getPotion().isBeneficial()) ? 50 : 25;
        }

        return 0;
    }

}