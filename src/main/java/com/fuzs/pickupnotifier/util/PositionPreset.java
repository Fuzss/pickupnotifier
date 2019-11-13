package com.fuzs.pickupnotifier.util;

@SuppressWarnings("unused")
public enum PositionPreset {

    TOP_LEFT(0, 0, false),
    TOP_RIGHT(1, 0, true),
    BOTTOM_LEFT(0, 1, false),
    BOTTOM_RIGHT(1, 1, false);

    private int x;
    private int y;
    private boolean shift;

    PositionPreset(int x, int y, boolean potionShift) {
        this.x = x;
        this.y = y;
        this.shift = potionShift;
    }

    public boolean isMirrored() {
        return this.x == 1;
    }

    public boolean isBottom() {
        return this.y == 1;
    }

    public boolean shouldShift() {
        return this.shift;
    }

    public int getX(int textureWidth, int scaledWidth, int offset) {
        return Math.abs((scaledWidth - textureWidth) * this.x - offset);
    }

    public int getY(int textureHeight, int scaledHeight, int offset) {
        return Math.abs((scaledHeight - textureHeight) * this.y - offset);
    }

}
