package fuzs.pickupnotifier.config;

import com.google.common.collect.Lists;
import fuzs.pickupnotifier.client.gui.PositionPreset;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigHolder {

    private static final GeneralConfig GENERAL_CONFIG = new GeneralConfig();
    private static final BehaviorConfig BEHAVIOR_CONFIG = new BehaviorConfig();
    private static final DisplayConfig DISPLAY_CONFIG = new DisplayConfig();

    private ConfigHolder() {

    }

    public static GeneralConfig getGeneralConfig() {

        return GENERAL_CONFIG;
    }

    public static BehaviorConfig getBehaviorConfig() {

        return BEHAVIOR_CONFIG;
    }

    public static DisplayConfig getDisplayConfig() {

        return DISPLAY_CONFIG;
    }

    private abstract static class AbstractModConfig {
        
        private final String name;
        private final ModConfig.Type type;

        public AbstractModConfig(String name, ModConfig.Type type) {
            
            this.name = name;
            this.type = type;
        }

        public final void setupConfig(ForgeConfigSpec.Builder builder) {

            builder.push(this.name);
            this.addToBuilder(builder);
            builder.pop();
        }
        
        protected abstract void addToBuilder(ForgeConfigSpec.Builder builder);

        protected <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerEntry(S entry, Consumer<T> action) {

            ConfigManager.registerEntry(this.type, entry, action);
        }

    }

    public static class GeneralConfig extends AbstractModConfig {

        public boolean displayExperience;
        public boolean logEverything;
        public boolean backpackCompat;
        public boolean clientSideOnly;
        
        private GeneralConfig() {
            
            super("general", ModConfig.Type.COMMON);
        }

        @Override
        public void addToBuilder(ForgeConfigSpec.Builder builder) {

            this.registerEntry(builder.comment("Include experience orbs the player has collected as part of the list of entries.").define("Display Experience", true), v -> this.displayExperience = v);
            this.registerEntry(builder.comment("Show entries for every item picked up, even the ones not going into the player inventory, e. g. when directly collected by backpacks.").define("Log Everything", true), v -> this.logEverything = v);
            this.registerEntry(builder.comment("Show entries for items picked up that don't go to the player's inventory. This will enable compatibility with some backpack mods, but might also falsely log items the player never actually receives.").define("Backpack Compatibility", false), v -> this.backpackCompat = v);
        }

    }

    public static class BehaviorConfig extends AbstractModConfig {

        public Set<Item> blacklist;
        public boolean combineEntries;
        public int displayTime;
        public boolean move;
        public int moveTime;
        public boolean fadeAway;
        public int maxCount;

        private BehaviorConfig() {

            super("behavior", ModConfig.Type.COMMON);
        }

        @Override
        public void addToBuilder(ForgeConfigSpec.Builder builder) {

            this.registerEntry(builder.comment("Disable specific items or content from whole mods from showing.", EntryCollectionBuilder.CONFIG_STRING).define("Blacklist", Lists.<String>newArrayList()), v -> this.blacklist = new EntryCollectionBuilder<>(ForgeRegistries.ITEMS).buildEntrySet(v));
            this.registerEntry(builder.comment("Combine entries of the same type instead of showing each one individually.").define("Combine Entries", true), v -> this.combineEntries = v);
            this.registerEntry(builder.comment("Amount of ticks each entry will be shown for. Set to 0 to only remove entries when space for new ones is needed.").defineInRange("Display Time", 40, 0, Integer.MAX_VALUE), v -> this.displayTime = v);
            this.registerEntry(builder.comment("Make outdated entries slowly move out of the screen instead of disappearing in place.").define("Move Out Of Screen", true), v -> this.move = v);
            this.registerEntry(builder.comment("Amount of ticks it takes for an entry to move out of the screen. Value cannot be larger than \"Display Time\".").defineInRange("Move Time", 20, 0, Integer.MAX_VALUE), v -> this.moveTime = v);
            this.registerEntry(builder.comment("Make outdated entry names slowly fade away instead of simply vanishing.").define("Fade Away", true), v -> this.fadeAway = v);
            this.registerEntry(builder.comment("Maximum count number displayed. Setting this to 0 will prevent the count from being displayed at all.").defineInRange("Maximum Amount", 9999, 0, Integer.MAX_VALUE), v -> this.maxCount = v);
        }

    }

    public static class DisplayConfig extends AbstractModConfig {

        public boolean showSprite;
        public ChatFormatting textColor;
        public boolean ignoreRarity;
        public PositionPreset position;
        public int xOffset;
        public int yOffset;
        public double height;
        public int scale;
        
        private DisplayConfig() {
            
            super("display", ModConfig.Type.COMMON);
        }

        @Override
        public void addToBuilder(ForgeConfigSpec.Builder builder) {

            this.registerEntry(builder.comment("Show a small sprite next to the name of each entry showing its contents.").define("Draw Sprites", true), v -> this.showSprite = v);
            this.registerEntry(builder.comment("Color of the entry name text.").defineEnum("Default Color", ChatFormatting.WHITE, Stream.of(ChatFormatting.values()).filter(ChatFormatting::isColor).collect(Collectors.toList())), v -> this.textColor = v);
            this.registerEntry(builder.comment("Ignore rarity of items and always use color specified in \"Text Color\" instead.").define("Ignore Rarity", false), v -> this.ignoreRarity = v);
            this.registerEntry(builder.comment("Screen corner for entry list to be drawn in.").defineEnum("Screen Corner", PositionPreset.BOTTOM_RIGHT), v -> this.position = v);
            this.registerEntry(builder.comment("Offset on x-axis from screen border.").defineInRange("X-Offset", 8, 0, Integer.MAX_VALUE), v -> this.xOffset = v);
            this.registerEntry(builder.comment("Offset on y-axis from screen border.").defineInRange("Y-Offset", 4, 0, Integer.MAX_VALUE), v -> this.yOffset = v);
            this.registerEntry(builder.comment("Percentage of relative screen height entries are allowed to fill at max.").defineInRange("Maximum Height", 0.5, 0.0, 1.0), v -> this.height = v);
            this.registerEntry(builder.comment("Scale of entries. A lower scale will make room for more rows to show. Works in tandem with \"GUI Scale\" option in \"Video Settings\".").defineInRange("Custom Scale", 4, 1, 24), v -> this.scale = v);
        }

    }

}
