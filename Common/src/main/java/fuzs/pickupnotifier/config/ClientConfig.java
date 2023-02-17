package fuzs.pickupnotifier.config;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.gui.PositionPreset;
import fuzs.puzzleslib.config.ConfigCore;
import fuzs.puzzleslib.config.ValueCallback;
import fuzs.puzzleslib.config.annotation.Config;
import fuzs.puzzleslib.config.core.AbstractConfigBuilder;
import net.minecraft.ChatFormatting;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientConfig implements ConfigCore {
    @Config
    public GeneralConfig general = new GeneralConfig();
    @Config
    public BehaviorConfig behavior = new BehaviorConfig();
    @Config
    public DisplayConfig display = new DisplayConfig();

    public static class GeneralConfig implements ConfigCore {
        public boolean forceClient;
        public boolean logItems;
        public boolean logExperience;
        public boolean logArrows;
        @Config(description = "Show the value of experience points collected instead of the amount of individual orbs.")
        public boolean experienceValue = true;
        @Config(description = "Prevent items from being added to the pick-up log when in creative mode.")
        public boolean disableInCreative = false;

        @Override
        public void addToBuilder(AbstractConfigBuilder builder, ValueCallback callback) {
            callback.accept(builder.comment(String.format("Force-run %s on the client-side only.", PickUpNotifier.MOD_NAME), "Only enable this when there are problems, e. g. the same pick-up being logged multiple times. Partial item stack pick-ups (when the inventory is full) won't show, and backpack compat won't work.", "When playing on a server without this mod this option will be used automatically (for technical reasons).").define("force_client_only", false), v -> this.forceClient = v);
            callback.accept(builder.comment("Show item entities the player has collected in the pick-up notifications.").define("include_items", true), v -> this.logItems = v);
            callback.accept(builder.comment("Show experience orbs the player has collected in the pick-up notifications.").define("include_experience", true), v -> this.logExperience = v);
            callback.accept(builder.comment("Show shot arrows the player has collected in the pick-up notifications.").define("include_arrows", true), v -> this.logArrows = v);
        }
    }

    public static class BehaviorConfig implements ConfigCore {
        public boolean combineEntries;
        public int displayTime;
        public boolean move;
        public int moveTime;
        public boolean fadeAway;
        public int maxCount;

        @Override
        public void addToBuilder(AbstractConfigBuilder builder, ValueCallback callback) {
            callback.accept(builder.comment("Combine entries of the same type instead of showing each one individually.").define("combine_entries", true), v -> this.combineEntries = v);
            callback.accept(builder.comment("Amount of ticks each entry will be shown for. Set to 0 to only remove entries when space for new ones is needed.").defineInRange("display_time", 80, 0, Integer.MAX_VALUE), v -> this.displayTime = v);
            callback.accept(builder.comment("Make outdated entries slowly move out of the screen instead of disappearing in place.").define("move_out_of_screen", true), v -> this.move = v);
            callback.accept(builder.comment("Amount of ticks it takes for an entry to move out of the screen. Value cannot be larger than \"Display Time\".").defineInRange("move_time", 20, 0, Integer.MAX_VALUE), v -> this.moveTime = v);
            callback.accept(builder.comment("Make outdated entry names slowly fade away instead of simply vanishing.").define("fade_away", true), v -> this.fadeAway = v);
            callback.accept(builder.comment("Maximum count number displayed. Setting this to 0 will prevent the count from being displayed at all.").defineInRange("maximum_amount", 9999, 0, Integer.MAX_VALUE), v -> this.maxCount = v);
        }

    }

    public static class DisplayConfig implements ConfigCore {
        public boolean showSprite;
        public ChatFormatting textColor;
        public boolean ignoreRarity;
        public PositionPreset position;
        public int xOffset;
        public int yOffset;
        public double height;
        public int scale;

        @Override
        public void addToBuilder(AbstractConfigBuilder builder, ValueCallback callback) {
            callback.accept(builder.comment("Show a small sprite next to the name of each entry showing its contents.").define("draw_sprites", true), v -> this.showSprite = v);
            callback.accept(builder.comment("Color of the entry name text.").defineEnum("default_color", ChatFormatting.WHITE, Stream.of(ChatFormatting.values()).filter(ChatFormatting::isColor).collect(Collectors.toList())), v -> this.textColor = v);
            callback.accept(builder.comment("Ignore rarity of items and always use color specified in \"Text Color\" instead.").define("ignore_rarity", false), v -> this.ignoreRarity = v);
            callback.accept(builder.comment("Screen corner for entry list to be drawn in.").defineEnum("screen_corner", PositionPreset.BOTTOM_RIGHT), v -> this.position = v);
            callback.accept(builder.comment("Offset on x-axis from screen border.").defineInRange("x_offset", 8, 0, Integer.MAX_VALUE), v -> this.xOffset = v);
            callback.accept(builder.comment("Offset on y-axis from screen border.").defineInRange("y_offset", 4, 0, Integer.MAX_VALUE), v -> this.yOffset = v);
            callback.accept(builder.comment("Percentage of relative screen height entries are allowed to fill at max.").defineInRange("maximum_height", 0.5, 0.0, 1.0), v -> this.height = v);
            callback.accept(builder.comment("Scale of entries. A lower scale will make room for more rows to show. Works together with \"GUI Scale\" option in \"Video Settings\".").defineInRange("custom_scale", 4, 1, 24), v -> this.scale = v);
        }
    }
}
