package com.fuzs.pickupnotifier.config;

import com.fuzs.pickupnotifier.PickUpNotifier;
import com.fuzs.pickupnotifier.client.gui.PositionPreset;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConfigValueHolder {

    private static final GeneralConfig GENERAL_CONFIG = new GeneralConfig();
    private static final DisplayConfig DISPLAY_CONFIG = new DisplayConfig();

    private ConfigValueHolder() {

    }

    public static GeneralConfig getGeneralConfig() {

        return GENERAL_CONFIG;
    }

    public static DisplayConfig getDisplayConfig() {

        return DISPLAY_CONFIG;
    }

    private abstract static class AbstractConfig {

        protected abstract String getName();

        abstract void setupConfig(ForgeConfigSpec.Builder builder);

        protected static <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerClientEntry(S entry, Consumer<T> action) {

            ConfigSyncManager.registerEntry(ModConfig.Type.CLIENT, entry, action);
        }

    }

    public static class GeneralConfig extends AbstractConfig {

        private final TextFormatting defaultTextColor = TextFormatting.WHITE;

        public Set<Item> blacklist;
        public boolean showSprite;
        public TextFormatting textColor;
        public boolean ignoreRarity;
        public boolean combineEntries;
        public int displayTime;
        public boolean move;
        public int moveTime;
        public boolean fadeAway;
        public int maxCount;
        public boolean displayExperience;

        @Override
        protected String getName() {

            return "general";
        }

        @Override
        public void setupConfig(ForgeConfigSpec.Builder builder) {

            builder.push(this.getName());

            registerClientEntry(builder.comment("Disable specific items or content from whole mods from showing.", "Format for every entry is \"<namespace>:<path>\". Path may use single asterisk as wildcard parameter.").define("Blacklist", new ArrayList<String>()),
                    v -> this.blacklist = new EntryCollectionBuilder<>(ForgeRegistries.ITEMS, PickUpNotifier.LOGGER).buildEntrySet(v));
            registerClientEntry(builder.comment("Show a small sprite next to the name of each entry showing its contents.").define("Draw Sprites", true), v -> this.showSprite = v);
            registerClientEntry(builder.comment("Color of the entry name text.", "Allowed Values: " + Arrays.stream(TextFormatting.values()).filter(TextFormatting::isColor).map(Enum::name).collect(Collectors.joining(", "))).define("Default Color", this.defaultTextColor.name()), v -> {

                try {

                    TextFormatting textColor = TextFormatting.valueOf(v);
                    if (!textColor.isColor()) {

                        throw new IllegalArgumentException("No text color " + textColor.getClass().getName() + "." + textColor.name());
                    }

                    this.textColor = textColor;
                } catch (IllegalArgumentException e) {

                    PickUpNotifier.LOGGER.error(e);
                    this.textColor = this.defaultTextColor;
                }
            });
            registerClientEntry(builder.comment("Ignore rarity of items and always use color specified in \"Text Color\" instead.").define("Ignore Rarity", false), v -> this.ignoreRarity = v);
            registerClientEntry(builder.comment("Combine entries of the same type instead of showing each one individually.").define("Combine Entries", true), v -> this.combineEntries = v);
            registerClientEntry(builder.comment("Amount of ticks each entry will be shown for. Set to 0 to only remove entries when space for new ones is needed.").defineInRange("Display Time", 80, 0, Integer.MAX_VALUE), v -> this.displayTime = v);
            registerClientEntry(builder.comment("Make outdated entries slowly move out of the screen instead of disappearing in place.").define("Move Out Of Screen", true), v -> this.move = v);
            registerClientEntry(builder.comment("Amount of ticks it takes for an entry to move out of the screen. Value cannot be larger than \"Display Time\".").defineInRange("Move Time", 20, 0, Integer.MAX_VALUE), v -> this.moveTime = v);
            registerClientEntry(builder.comment("Make outdated entry names slowly fade away instead of simply vanishing.").define("Fade Away", true), v -> this.fadeAway = v);
            registerClientEntry(builder.comment("Maximum count number displayed. Setting this to 0 will prevent the count from being displayed at all.").defineInRange("Maximum Amount", 9999, 0, Integer.MAX_VALUE), v -> this.maxCount = v);
            registerClientEntry(builder.comment("Include experience orbs the player has collected as part of the list of entries.").define("Display Experience", true), v -> this.displayExperience = v);

            builder.pop();
        }

    }

    public static class DisplayConfig extends AbstractConfig {

        public PositionPreset position;
        public int xOffset;
        public int yOffset;
        public double height;
        public int scale;

        @Override
        protected String getName() {

            return "display";
        }

        @Override
        public void setupConfig(ForgeConfigSpec.Builder builder) {

            builder.push(this.getName());

            registerClientEntry(builder.comment("Screen corner for entry list to be drawn in.").defineEnum("Screen Corner", PositionPreset.BOTTOM_RIGHT), v -> this.position = v);
            registerClientEntry(builder.comment("Offset on x-axis from screen border.").defineInRange("X-Offset", 8, 0, Integer.MAX_VALUE), v -> this.xOffset = v);
            registerClientEntry(builder.comment("Offset on y-axis from screen border.").defineInRange("Y-Offset", 4, 0, Integer.MAX_VALUE), v -> this.yOffset = v);
            registerClientEntry(builder.comment("Percentage of relative screen height entries are allowed to fill at max.").defineInRange("Maximum Height", 0.5, 0.0, 1.0), v -> this.height = v);
            registerClientEntry(builder.comment("Scale of entries. A lower scale will make room for more rows to show. Works in tandem with \"GUI Scale\" option in \"Video Settings\".").defineInRange("Custom Scale", 4, 1, 24), v -> this.scale = v);

            builder.pop();

        }

    }

}
