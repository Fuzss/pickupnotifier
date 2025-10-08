package fuzs.pickupnotifier.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import fuzs.puzzleslib.api.config.v3.serialization.KeyedValueProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
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
        @Config(description = {
                "Force the mod to run client-side only.",
                "Only enable this when there are problems, e. g. the same pick-up being logged multiple times. Partial item stack pick-ups (when the inventory is full) won't show, and backpack compatibility won't work.",
                "When playing on a server without this mod this option will be used automatically."
        })
        public boolean clientOnly = false;
        @Config(description = "Show item entities the player has collected in the pick-up notifications.")
        public boolean includeItems = true;
        @Config(description = "Show experience orbs the player has collected in the pick-up notifications.")
        public boolean includeExperience = true;
        @Config(description = "Show shot arrows the player has collected in the pick-up notifications.")
        public boolean includeArrows = true;
        @Config(description = "Show the value of experience points collected instead of the amount of individual orbs.")
        public boolean experienceValue = true;
        @Config(description = "Prevent items from being added to the pick-up log when in creative mode.")
        public boolean disableInCreative = false;
        @Config(name = "hidden_items", description = {
                "Disable specific items or content from whole mods from showing.", ConfigDataSet.CONFIG_DESCRIPTION
        })
        List<String> hiddenItemsRaw = KeyedValueProvider.tagAppender(Registries.ITEM).asStringList();

        public ConfigDataSet<Item> hiddenItems;

        @Override
        public void afterConfigReload() {
            this.hiddenItems = ConfigDataSet.from(Registries.ITEM, this.hiddenItemsRaw);
        }
    }

    public static class BehaviorConfig implements ConfigCore {
        @Config(description = "Combine entries of the same type instead of showing each one individually.")
        public CombineEntries combineEntries = CombineEntries.EXCLUDE_NAMED;
        @Config(description = "Amount of ticks each entry will be shown for. Set to zero to only remove entries when space for new ones is needed.")
        @Config.IntRange(min = 0)
        public int displayTime = 80;
        @Config(description = {
                "Make outdated entries slowly move out of the screen.",
                "Not necessarily supported by all position presets."
        })
        public MoveOut moveOut = MoveOut.VERTICALLY_ONLY;
        @Config(description = "Amount of ticks it takes for an entry to move out of the screen.")
        @Config.IntRange(min = 0)
        int moveTime = 20;
        @Config(description = "Make outdated entry names slowly fade away instead of simply vanishing.")
        public boolean fadeOut = true;

        public int getMoveTime() {
            return Math.min(this.moveTime, this.displayTime);
        }
    }

    public static class DisplayConfig implements ConfigCore {
        public boolean drawSprite;
        public ChatFormatting textColor;
        public boolean ignoreRarity;
        public AnchorPoint position;
        public int offsetX;
        public int offsetY;
        public double maxHeight;
        int displayScale;
        @Config(description = "Where and if to display the amount of items picked up. 'SPRITE' will render the amount on the item sprite like in inventories, 'TEXT' will add a dedicated text including the amount to the item name display.")
        public DisplayAmount displayAmount = DisplayAmount.TEXT;
        @Config(description = "Add the total amount of an item in your inventory to the entry.")
        public boolean inventoryCount = false;
        @Config(description = "Should the picked up amount be shown when it's just a single item.")
        public boolean displaySingleCount = true;
        @Config(description = "Mode for drawing a background behind entries for better visibility. 'CHAT' is similar to the chat background, 'TOOLTIP' uses the tooltip background rendering instead.")
        public EntryBackground entryBackground = EntryBackground.CHAT;
        @Config(description = "Add the name of the item to the entry.")
        public boolean displayItemName = true;

        @Override
        public void addToBuilder(ModConfigSpec.Builder builder, ValueCallback callback) {
            callback.accept(builder.comment("Show a small sprite next to the name of each entry showing its contents.")
                    .define("draw_sprite", true), v -> this.drawSprite = v);
            callback.accept(builder.comment("Color of the entry name text.")
                    .defineEnum("default_text_color",
                            ChatFormatting.WHITE,
                            Stream.of(ChatFormatting.values())
                                    .filter(ChatFormatting::isColor)
                                    .collect(Collectors.toList())), v -> this.textColor = v);
            callback.accept(builder.comment("Ignore rarity when determining item name color.")
                    .define("ignore_rarity", false), v -> this.ignoreRarity = v);
            callback.accept(builder.comment("Screen corner for entry list to be drawn in.")
                    .defineEnum("screen_corner", AnchorPoint.BOTTOM_RIGHT), v -> this.position = v);
            callback.accept(builder.comment("Offset on x-axis from screen border.")
                    .defineInRange("offset_x", 8, 0, Integer.MAX_VALUE), v -> this.offsetX = v);
            callback.accept(builder.comment("Offset on y-axis from screen border.")
                    .defineInRange("offset_y", 4, 0, Integer.MAX_VALUE), v -> this.offsetY = v);
            callback.accept(builder.comment("Percentage of relative screen height entries are allowed to fill at max.")
                    .defineInRange("max_height", 0.5, 0.0, 1.0), v -> this.maxHeight = v);
            callback.accept(builder.comment(
                            "Scale of entries. A lower scale will make room for more rows to show. Works together with \"GUI Scale\" option in \"Video Settings\".")
                    .defineInRange("display_scale", 4, 1, 24), v -> this.displayScale = v);
        }

        public float getDisplayScale() {
            return this.displayScale / 6.0F;
        }
    }
}
