package fuzs.pickupnotifier.client.gui;

public enum PositionPreset {
    TOP_LEFT(0, 0),
    TOP_RIGHT(1, 0),
    BOTTOM_LEFT(0, 1),
    BOTTOM_RIGHT(1, 1);

    private final int posX;
    private final int posY;

    PositionPreset(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public boolean mirrored() {
        return this.posX == 1;
    }

    public boolean bottom() {
        return this.posY == 1;
    }

    public int getX(int textureWidth, int scaledWidth, int offset) {
        return Math.abs((scaledWidth - textureWidth) * this.posX - offset);
    }

    public int getY(int textureHeight, int scaledHeight, int offset) {
        return Math.abs((scaledHeight - textureHeight) * this.posY - offset);
    }
}