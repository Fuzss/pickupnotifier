package fuzs.pickupnotifier.config;

import com.google.common.collect.Lists;
import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.gui.PositionPreset;
import fuzs.pickupnotifier.config.core.AbstractConfig;
import fuzs.pickupnotifier.config.serialization.EntryCollectionBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientConfig extends AbstractConfig.AbstractClientConfig {

    private final GeneralConfig general = new GeneralConfig();
    private final BehaviorConfig behavior = new BehaviorConfig();
    private final DisplayConfig display = new DisplayConfig();

    @Override
    protected void addToBuilder(ForgeConfigSpec.Builder builder) {

        setupConfig(this.general, builder);
        setupConfig(this.behavior, builder);
        setupConfig(this.display, builder);
    }

    public GeneralConfig general() {

        return this.general;
    }

    public BehaviorConfig behavior() {

        return this.behavior;
    }

    public DisplayConfig display() {

        return this.display;
    }

    public static class GeneralConfig extends AbstractConfig {

        public boolean forceClient;
        public boolean logItems;
        public boolean logExperience;
        public boolean logArrows;

        private GeneralConfig() {

            super("general");
        }

        @Override
        public void addToBuilder(ForgeConfigSpec.Builder builder) {

            register(builder.comment("Force-run " + PickUpNotifier.NAME + " on the client-side only.", "Only enable this when there are problems, e. g. the same pick-up being logged multiple times. Partial item stack pick-ups (when the inventory is full) won't show, and backpack compat won't work.", "When playing on a server without this mod this option will be used automatically (for technical reasons).").define("Force Client Only", false), v -> this.forceClient = v);
            register(builder.comment("Include item entities the player has collected in the pick-ups list.").define("Log Items", true), v -> this.logItems = v);
            register(builder.comment("Include experience orbs the player has collected in the pick-ups list.").define("Log Experience", true), v -> this.logExperience = v);
            register(builder.comment("Include shot arrows the player has collected in the pick-ups list.").define("Log Arrows", true), v -> this.logArrows = v);
        }

    }

    public static class BehaviorConfig extends AbstractConfig {

        public Set<Item> blacklist;
        public boolean combineEntries;
        public int displayTime;
        public boolean move;
        public int moveTime;
        public boolean fadeAway;
        public int maxCount;

        private BehaviorConfig() {

            super("behavior");
        }

        @Override
        public void addToBuilder(ForgeConfigSpec.Builder builder) {

            register(builder.comment("Disable specific items or content from whole mods from showing.", EntryCollectionBuilder.CONFIG_STRING).define("Blacklist", Lists.<String>newArrayList()), v -> this.blacklist = new EntryCollectionBuilder<>(ForgeRegistries.ITEMS).buildEntrySet(v));
            register(builder.comment("Combine entries of the same type instead of showing each one individually.").define("Combine Entries", true), v -> this.combineEntries = v);
            register(builder.comment("Amount of ticks each entry will be shown for. Set to 0 to only remove entries when space for new ones is needed.").defineInRange("Display Time", 80, 0, Integer.MAX_VALUE), v -> this.displayTime = v);
            register(builder.comment("Make outdated entries slowly move out of the screen instead of disappearing in place.").define("Move Out Of Screen", true), v -> this.move = v);
            register(builder.comment("Amount of ticks it takes for an entry to move out of the screen. Value cannot be larger than \"Display Time\".").defineInRange("Move Time", 20, 0, Integer.MAX_VALUE), v -> this.moveTime = v);
            register(builder.comment("Make outdated entry names slowly fade away instead of simply vanishing.").define("Fade Away", true), v -> this.fadeAway = v);
            register(builder.comment("Maximum count number displayed. Setting this to 0 will prevent the count from being displayed at all.").defineInRange("Maximum Amount", 9999, 0, Integer.MAX_VALUE), v -> this.maxCount = v);
        }

    }

    public static class DisplayConfig extends AbstractConfig {

        public boolean showSprite;
        public ChatFormatting textColor;
        public boolean ignoreRarity;
        public PositionPreset position;
        public int xOffset;
        public int yOffset;
        public double height;
        public int scale;

        private DisplayConfig() {

            super("display");
        }

        @Override
        public void addToBuilder(ForgeConfigSpec.Builder builder) {

            register(builder.comment("Show a small sprite next to the name of each entry showing its contents.").define("Draw Sprites", true), v -> this.showSprite = v);
            register(builder.comment("Color of the entry name text.").defineEnum("Default Color", ChatFormatting.WHITE, Stream.of(ChatFormatting.values()).filter(ChatFormatting::isColor).collect(Collectors.toList())), v -> this.textColor = v);
            register(builder.comment("Ignore rarity of items and always use color specified in \"Text Color\" instead.").define("Ignore Rarity", false), v -> this.ignoreRarity = v);
            register(builder.comment("Screen corner for entry list to be drawn in.").defineEnum("Screen Corner", PositionPreset.BOTTOM_RIGHT), v -> this.position = v);
            register(builder.comment("Offset on x-axis from screen border.").defineInRange("X-Offset", 8, 0, Integer.MAX_VALUE), v -> this.xOffset = v);
            register(builder.comment("Offset on y-axis from screen border.").defineInRange("Y-Offset", 4, 0, Integer.MAX_VALUE), v -> this.yOffset = v);
            register(builder.comment("Percentage of relative screen height entries are allowed to fill at max.").defineInRange("Maximum Height", 0.5, 0.0, 1.0), v -> this.height = v);
            register(builder.comment("Scale of entries. A lower scale will make room for more rows to show. Works together with \"GUI Scale\" option in \"Video Settings\".").defineInRange("Custom Scale", 4, 1, 24), v -> this.scale = v);
        }

    }

}
