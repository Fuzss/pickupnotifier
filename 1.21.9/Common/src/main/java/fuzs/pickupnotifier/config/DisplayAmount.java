package fuzs.pickupnotifier.config;

public enum DisplayAmount {
    OFF(false, false),
    SPRITE(true, false),
    TEXT(false, true),
    BOTH(true, true);

    private final boolean isSprite;
    private final boolean isText;

    DisplayAmount(boolean isSprite, boolean isText) {
        this.isSprite = isSprite;
        this.isText = isText;
    }

    public boolean isSprite() {
        return this.isSprite;
    }

    public boolean isText() {
        return this.isText;
    }
}
