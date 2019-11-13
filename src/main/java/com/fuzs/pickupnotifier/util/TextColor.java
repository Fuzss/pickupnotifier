package com.fuzs.pickupnotifier.util;

import net.minecraft.util.text.TextFormatting;

@SuppressWarnings("unused")
public enum TextColor {

    WHITE(TextFormatting.WHITE),
    ORANGE(TextFormatting.GOLD),
    MAGENTA(TextFormatting.AQUA),
    LIGHT_BLUE(TextFormatting.BLUE),
    YELLOW(TextFormatting.YELLOW),
    LIME(TextFormatting.GREEN),
    PINK(TextFormatting.LIGHT_PURPLE),
    GRAY(TextFormatting.DARK_GRAY),
    LIGHT_GRAY(TextFormatting.GRAY),
    CYAN(TextFormatting.DARK_AQUA),
    PURPLE(TextFormatting.DARK_PURPLE),
    BLUE(TextFormatting.DARK_BLUE),
    BROWN(TextFormatting.RED),
    GREEN(TextFormatting.DARK_GREEN),
    RED(TextFormatting.DARK_RED),
    BLACK(TextFormatting.BLACK);

    private final TextFormatting chatColor;

    TextColor(TextFormatting chatColorIn) {
        this.chatColor = chatColorIn;
    }

    public TextFormatting getChatColor() {
        return this.chatColor;
    }

}
