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

    public static class GeneralConfig {

        public final ForgeConfigSpec.ConfigValue<List<String>> blacklist;
        public final ForgeConfigSpec.IntValue displayTime;
        public final ForgeConfigSpec.IntValue fadeTime;
        public final ForgeConfigSpec.BooleanValue fade;
        public final ForgeConfigSpec.BooleanValue showSprite;
        public final ForgeConfigSpec.EnumValue<TextColor> color;
        public final ForgeConfigSpec.BooleanValue ignoreRarity;
        public final ForgeConfigSpec.IntValue scale;
        public final ForgeConfigSpec.EnumValue<PositionPreset> position;
        public final ForgeConfigSpec.IntValue xOffset;
        public final ForgeConfigSpec.IntValue yOffset;
        public final ForgeConfigSpec.DoubleValue displayHeight;

        private GeneralConfig(String name) {
            BUILDER.push(name);
            this.blacklist = ConfigBuildHandler.BUILDER.comment("Disable specific items or content from whole mods from showing. Enter as either \"modid:item\" or \"modid\".").define("Blacklist", Lists.newArrayList("examplemod", "examplemod:exampleitem"));
            this.displayTime = ConfigBuildHandler.BUILDER.comment("Amount of ticks each item entry will be shown for.").defineInRange("Display Time", 80, 0, Integer.MAX_VALUE);
            this.fadeTime = ConfigBuildHandler.BUILDER.comment("Amount of ticks it takes for an item entry to move out of the screen. Value cannot be larger than \"Display Time\".").defineInRange("Fade Time", 20, 0, Integer.MAX_VALUE);
            this.fade = ConfigBuildHandler.BUILDER.comment("Should outdated item entries fade away instead of simply vanishing. Only the name will fade.").define("Fade", false);
            this.showSprite = ConfigBuildHandler.BUILDER.comment("Show a small sprite next to the name of each item.").define("Draw Sprites", true);
            this.color = ConfigBuildHandler.BUILDER.comment("Color of the item name text.").defineEnum("Text Color", TextColor.WHITE);
            this.ignoreRarity = ConfigBuildHandler.BUILDER.comment("Ignore rarity of items and always use color specified in \"Text Color\" instead.").define("Ignore Rarity", false);
            this.scale = ConfigBuildHandler.BUILDER.comment("Scale of item entries. A lower scale will make room for more rows to show. Works in tandem with \"GUI Scale\" option in \"Video Settings\".").defineInRange("Custom Scale", 4, 1, 24);
            this.position = ConfigBuildHandler.BUILDER.comment("Screen corner for item entries to be drawn in.").defineEnum("Screen Corner", PositionPreset.BOTTOM_RIGHT);
            this.xOffset = ConfigBuildHandler.BUILDER.comment("Offset on x-axis from screen border.").defineInRange("X-Offset", 8, 0, Integer.MAX_VALUE);
            this.yOffset = ConfigBuildHandler.BUILDER.comment("Offset on y-axis from screen border.").defineInRange("Y-Offset", 4, 0, Integer.MAX_VALUE);
            this.displayHeight = ConfigBuildHandler.BUILDER.comment("Percentage of relative screen height item entries are allowed to fill at max.").defineInRange("Maximum Height", 0.5, 0.0, 1.0);
            BUILDER.pop();
        }

    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

}
