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
    @Config.Name("display")
    public static DisplayConfig displayConfig = new DisplayConfig();

    public static class GeneralConfig {

        @Config.Name("Blacklist")
        @Config.Comment("Disable specific items or content from whole mods from showing. Enter as either \"modid:item\" or \"modid\".")
        public String[] blacklist = new String[]{"examplemod", "examplemod:exampleitem"};
        @Config.Name("Draw Sprites")
        @Config.Comment("Show a small sprite next to the name of each item.")
        public boolean showSprite = true;
        @Config.Name("Text Color")
        @Config.Comment("Color of the item name text.")
        public TextColor color = TextColor.WHITE;
        @Config.Name("Ignore Rarity")
        @Config.Comment("Ignore rarity of items and always use color specified in \"Text Color\" instead.")
        public boolean ignoreRarity = false;
        @Config.Name("Combine Entries")
        @Config.Comment("Combine entries for equal items instead of showing each one individually.")
        public boolean combineEntries = true;
        @Config.Name("Display Time")
        @Config.Comment("Amount of ticks each item entry will be shown for.")
        @Config.RangeInt(min = 0)
        public int displayTime = 80;
        @Config.Name("Move Out Of Screen")
        @Config.Comment("Make outdated entries slowly move out of the screen instead of disappearing instantly.")
        public boolean move = true;
        @Config.Name("Move Time")
        @Config.Comment("Amount of ticks it takes for an item entry to move out of the screen. Value cannot be larger than \"Display Time\".")
        @Config.RangeInt(min = 0)
        public int moveTime = 20;
        @Config.Name("Move Force Fade")
        @Config.Comment("Force moving item entry names to slowly fade even when sprites are shown instead of simply vanishing.")
        public boolean moveFadeForce = false;

    }

    public static class DisplayConfig {

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
        @Config.Name("Custom Scale")
        @Config.Comment("Scale of item entries. A lower scale will make room for more rows to show. Works in tandem with \"GUI Scale\" option in \"Video Settings\".")
        @Config.RangeInt(min = 1, max = 24)
        public int scale = 4;

    }

}
