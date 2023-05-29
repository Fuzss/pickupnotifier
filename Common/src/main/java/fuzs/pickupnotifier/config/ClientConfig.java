package fuzs.pickupnotifier.config;

import fuzs.pickupnotifier.client.gui.PositionPreset;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import net.minecraft.ChatFormatting;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientConfig implements ConfigCore {
    @Config
    public final GeneralConfig general = new GeneralConfig();
    @Config
    public final BehaviorConfig behavior = new BehaviorConfig();
    @Config
    public final DisplayConfig display = new DisplayConfig();

    public static class GeneralConfig implements ConfigCore {
        public boolean forceClient;
        public boolean includeItems;
        public boolean includeExperience;
        public boolean includeArrows;
        @Config(description = "Show the value of experience points collected instead of the amount of individual orbs.")
        public boolean experienceValue = true;
        @Config(description = "Prevent items from being added to the pick-up log when in creative mode.")
        public boolean disableInCreative = false;

        @Override
        public void addToBuilder(ForgeConfigSpec.Builder builder, ValueCallback callback) {
            callback.accept(builder.comment("Force-run the mod on the client-side only.", "Only enable this when there are problems, e. g. the same pick-up being logged multiple times. Partial item stack pick-ups (when the inventory is full) won't show, and backpack compat won't work.", "When playing on a server without this mod this option will be used automatically.").define("force_client_only", false), v -> this.forceClient = v);
            callback.accept(builder.comment("Show item entities the player has collected in the pick-up notifications.").define("include_items", true), v -> this.includeItems = v);
            callback.accept(builder.comment("Show experience orbs the player has collected in the pick-up notifications.").define("include_experience", true), v -> this.includeExperience = v);
            callback.accept(builder.comment("Show shot arrows the player has collected in the pick-up notifications.").define("include_arrows", true), v -> this.includeArrows = v);
        }
    }

    public static class BehaviorConfig implements ConfigCore {
        public CombineEntries combineEntries;
        public int displayTime;
        public boolean move;
        public int moveTime;
        public boolean fadeAway;

        @Override
        public void addToBuilder(ForgeConfigSpec.Builder builder, ValueCallback callback) {
            callback.accept(builder.comment("Combine entries of the same type instead of showing each one individually.").defineEnum("combine_entries", CombineEntries.EXCLUDE_NAMED), v -> this.combineEntries = v);
            callback.accept(builder.comment("Amount of ticks each entry will be shown for. Set to 0 to only remove entries when space for new ones is needed.").defineInRange("display_time", 80, 0, Integer.MAX_VALUE), v -> this.displayTime = v);
            callback.accept(builder.comment("Make outdated entries slowly move out of the screen instead of disappearing in place.").define("move_out_of_screen", true), v -> this.move = v);
            callback.accept(builder.comment("Amount of ticks it takes for an entry to move out of the screen. Value cannot be larger than \"Display Time\".").defineInRange("move_time", 20, 0, Integer.MAX_VALUE), v -> this.moveTime = v);
            callback.accept(builder.comment("Make outdated entry names slowly fade away instead of simply vanishing.").define("fade_away", true), v -> this.fadeAway = v);
        }
    }

    public static class DisplayConfig implements ConfigCore {
        public boolean drawSprite;
        public ChatFormatting textColor;
        public boolean ignoreRarity;
        public PositionPreset position;
        public int offsetX;
        public int offsetY;
        public double maxHeight;
        public int scale;
        @Config(description = "Where and if to display the amount of items picked up. 'SPRITE' will render the amount on the item sprite like in inventories, 'TEXT' will add a dedicated text including the amount to the item name display.")
        public DisplayAmount displayAmount = DisplayAmount.TEXT;
        @Config(description = "Add the total amount of an item in your inventory to the entry.")
        public boolean inventoryCount = false;
        @Config(description = "Should the picked up amount be shown when it's just a single item.")
        public boolean displaySingleCount = true;
        @Config(description = "Mode for drawing a background behind entries for better visibility. 'CHAT' is similar to the chat background, 'TOOLTIP' uses the tooltip background rendering instead.")
        public EntryBackground entryBackground = EntryBackground.NONE;
        @Config(description = "Add the name of the item to the entry.")
        public boolean displayItemName = true;

        @Override
        public void addToBuilder(ForgeConfigSpec.Builder builder, ValueCallback callback) {
            callback.accept(builder.comment("Show a small sprite next to the name of each entry showing its contents.").define("draw_sprites", true), v -> this.drawSprite = v);
            callback.accept(builder.comment("Color of the entry name text.").defineEnum("default_color", ChatFormatting.WHITE, Stream.of(ChatFormatting.values()).filter(ChatFormatting::isColor).collect(Collectors.toList())), v -> this.textColor = v);
            callback.accept(builder.comment("Ignore rarity of items and always use color specified in \"Text Color\" instead.").define("ignore_rarity", false), v -> this.ignoreRarity = v);
            callback.accept(builder.comment("Screen corner for entry list to be drawn in.").defineEnum("screen_corner", PositionPreset.BOTTOM_RIGHT), v -> this.position = v);
            callback.accept(builder.comment("Offset on x-axis from screen border.").defineInRange("offset_x", 8, 0, Integer.MAX_VALUE), v -> this.offsetX = v);
            callback.accept(builder.comment("Offset on y-axis from screen border.").defineInRange("offset_y", 4, 0, Integer.MAX_VALUE), v -> this.offsetY = v);
            callback.accept(builder.comment("Percentage of relative screen height entries are allowed to fill at max.").defineInRange("max_height", 0.5, 0.0, 1.0), v -> this.maxHeight = v);
            callback.accept(builder.comment("Scale of entries. A lower scale will make room for more rows to show. Works together with \"GUI Scale\" option in \"Video Settings\".").defineInRange("scale", 4, 1, 24), v -> this.scale = v);
        }
    }

    public enum DisplayAmount {
        OFF, SPRITE, TEXT, BOTH;

        public boolean sprite() {

            return this == SPRITE || this == BOTH;
        }

        public boolean text() {

            return this == TEXT || this == BOTH;
        }
    }

    public enum EntryBackground {
        NONE, CHAT, TOOLTIP
    }

    public enum CombineEntries {
        ALWAYS, NEVER, EXCLUDE_NAMED
    }
}
