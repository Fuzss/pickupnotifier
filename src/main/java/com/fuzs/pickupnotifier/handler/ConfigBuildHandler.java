package com.fuzs.pickupnotifier.handler;

import com.fuzs.pickupnotifier.util.PositionPreset;
import com.fuzs.pickupnotifier.util.TextColor;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ConfigBuildHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig("general");
    public static final DisplayConfig DISPLAY_CONFIG = new DisplayConfig("display");

    public static class GeneralConfig {

        public final ForgeConfigSpec.ConfigValue<List<String>> blacklist;
        public final ForgeConfigSpec.BooleanValue showSprite;
        public final ForgeConfigSpec.EnumValue<TextColor> color;
        public final ForgeConfigSpec.BooleanValue ignoreRarity;
        public final ForgeConfigSpec.BooleanValue combineEntries;
        public final ForgeConfigSpec.IntValue displayTime;
        public final ForgeConfigSpec.BooleanValue move;
        public final ForgeConfigSpec.IntValue moveTime;
        public final ForgeConfigSpec.BooleanValue fadeAway;
        public final ForgeConfigSpec.IntValue maxCount;
        public final ForgeConfigSpec.BooleanValue displayExperience;

        private GeneralConfig(String name) {

            BUILDER.push(name);
            this.blacklist = ConfigBuildHandler.BUILDER.comment("Disable specific items or content from whole mods from showing. Enter as either \"modid:item\" or \"modid\".").define("Blacklist", Lists.newArrayList("examplemod", "examplemod:exampleitem"));
            this.showSprite = ConfigBuildHandler.BUILDER.comment("Show a small sprite next to the name of each entry showing its contents.").define("Draw Sprites", true);
            this.color = ConfigBuildHandler.BUILDER.comment("Color of the entry name text.").defineEnum("Text Color", TextColor.WHITE);
            this.ignoreRarity = ConfigBuildHandler.BUILDER.comment("Ignore rarity of items and always use color specified in \"Text Color\" instead.").define("Ignore Rarity", false);
            this.combineEntries = ConfigBuildHandler.BUILDER.comment("Combine entries of the same type instead of showing each one individually.").define("Combine Entries", true);
            this.displayTime = ConfigBuildHandler.BUILDER.comment("Amount of ticks each entry will be shown for. Set to 0 to only remove entries when space for new ones is needed.").defineInRange("Display Time", 80, 0, Integer.MAX_VALUE);
            this.move = ConfigBuildHandler.BUILDER.comment("Make outdated entries slowly move out of the screen instead of disappearing in place.").define("Move Out Of Screen", true);
            this.moveTime = ConfigBuildHandler.BUILDER.comment("Amount of ticks it takes for an entry to move out of the screen. Value cannot be larger than \"Display Time\".").defineInRange("Move Time", 20, 0, Integer.MAX_VALUE);
            this.fadeAway = ConfigBuildHandler.BUILDER.comment("Make outdated entry names slowly fade away instead of simply vanishing.").define("Fade Away", true);
            this.maxCount = ConfigBuildHandler.BUILDER.comment("Maximum count number displayed. Setting this to 0 will prevent the count from being displayed at all.").defineInRange("Maximum Amount", 9999, 0, Integer.MAX_VALUE);
            this.displayExperience = ConfigBuildHandler.BUILDER.comment("Include experience orbs the player has collected as part of the list of entries.").define("Display Experience", true);
            BUILDER.pop();

        }

    }

    public static class DisplayConfig {

        public final ForgeConfigSpec.EnumValue<PositionPreset> position;
        public final ForgeConfigSpec.IntValue xOffset;
        public final ForgeConfigSpec.IntValue yOffset;
        public final ForgeConfigSpec.DoubleValue height;
        public final ForgeConfigSpec.IntValue scale;

        private DisplayConfig(String name) {

            BUILDER.push(name);
            this.position = ConfigBuildHandler.BUILDER.comment("Screen corner for entry list to be drawn in.").defineEnum("Screen Corner", PositionPreset.BOTTOM_RIGHT);
            this.xOffset = ConfigBuildHandler.BUILDER.comment("Offset on x-axis from screen border.").defineInRange("X-Offset", 8, 0, Integer.MAX_VALUE);
            this.yOffset = ConfigBuildHandler.BUILDER.comment("Offset on y-axis from screen border.").defineInRange("Y-Offset", 4, 0, Integer.MAX_VALUE);
            this.height = ConfigBuildHandler.BUILDER.comment("Percentage of relative screen height entries are allowed to fill at max.").defineInRange("Maximum Height", 0.5, 0.0, 1.0);
            this.scale = ConfigBuildHandler.BUILDER.comment("Scale of entries. A lower scale will make room for more rows to show. Works in tandem with \"GUI Scale\" option in \"Video Settings\".").defineInRange("Custom Scale", 4, 1, 24);
            BUILDER.pop();

        }

    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

}
