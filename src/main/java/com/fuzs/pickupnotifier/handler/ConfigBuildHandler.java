package com.fuzs.pickupnotifier.handler;

import com.fuzs.pickupnotifier.PickUpNotifier;
import com.fuzs.pickupnotifier.util.PositionPreset;
import com.fuzs.pickupnotifier.util.TextColor;
import net.minecraftforge.common.config.Config;

@SuppressWarnings("WeakerAccess")
@Config(modid = PickUpNotifier.MODID)
public class ConfigBuildHandler {

    @Config.Name("general")
    public static GeneralConfig generalConfig = new GeneralConfig();

    public static class GeneralConfig {

        @Config.Name("Blacklist")
        @Config.Comment("Disable specific items or content from whole mods from showing. Enter as either \"modid:item\" or \"modid\".")
        public String[] blacklist = new String[]{"examplemod", "examplemod:exampleitem"};
        @Config.Name("Display Time")
        @Config.Comment("Amount of ticks each item entry will be shown for.")
        @Config.RangeInt(min = 0)
        public int displayTime = 80;
        @Config.Name("Fade Time")
        @Config.Comment("Amount of ticks it takes for an item entry to move out of the screen. Value cannot be larger than \"Display Time\".")
        @Config.RangeInt(min = 0)
        public int fadeTime = 20;
        @Config.Name("Force Fade")
        @Config.Comment("Force outdated item entry names to always fade away instead of simply vanishing, even when sprites are shown.")
        public boolean fadeForce = false;
        @Config.Name("Draw Sprites")
        @Config.Comment("Show a small sprite next to the name of each item.")
        public boolean showSprite = true;
        @Config.Name("Text Color")
        @Config.Comment("Color of the item name text.")
        public TextColor color = TextColor.WHITE;
        @Config.Name("Ignore Rarity")
        @Config.Comment("Ignore rarity of items and always use color specified in \"Text Color\" instead.")
        public boolean ignoreRarity = false;
        @Config.Name("Custom Scale")
        @Config.Comment("Scale of item entries. A lower scale will make room for more rows to show. Works in tandem with \"GUI Scale\" option in \"Video Settings\".")
        @Config.RangeInt(min = 1, max = 24)
        public int scale = 4;
        @Config.Name("Screen Corner")
        @Config.Comment("Screen corner for item entries to be drawn in.")
        public PositionPreset position = PositionPreset.BOTTOM_RIGHT;
        @Config.Name("X-Offset")
        @Config.Comment("Offset on x-axis from screen border.")
        @Config.RangeInt(min = 0)
        public int xOffset = 8;
        @Config.Name("Y-Offset")
        @Config.Comment("Offset on y-axis from screen border.")
        @Config.RangeInt(min = 0)
        public int yOffset = 4;
        @Config.Name("Maximum Height")
        @Config.Comment("Percentage of relative screen height item entries are allowed to fill at max.")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double displayHeight = 0.5;

    }

}
