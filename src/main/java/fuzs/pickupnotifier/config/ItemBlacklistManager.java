package fuzs.pickupnotifier.config;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fuzs.puzzleslib.config.serialization.EntryCollectionBuilder;
import fuzs.puzzleslib.json.JsonConfigFileUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.FileReader;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class ItemBlacklistManager {
    private static DimensionBlacklist defaultBlacklist = DimensionBlacklist.create(false);
    private static Map<ResourceKey<Level>, DimensionBlacklist> dimensionBlacklists = Maps.newIdentityHashMap();

    public static boolean isItemAllowed(ResourceKey<Level> dimension, Item item) {
        DimensionBlacklist blacklist = dimensionBlacklists.getOrDefault(dimension, defaultBlacklist);
        if (blacklist.inverted()) {
            return blacklist.items().contains(item);
        } else {
            return !blacklist.items().contains(item);
        }
    }

    public static void loadAll(String directory) {
        defaultBlacklist = DimensionBlacklist.create(false);
        dimensionBlacklists = Maps.newIdentityHashMap();
        JsonConfigFileUtil.getAllAndLoad(directory, file -> {}, ItemBlacklistManager::deserializeDataEntry, () -> {});
    }

    private static void deserializeDataEntry(FileReader reader) {
        JsonElement jsonElement = JsonConfigFileUtil.GSON.fromJson(reader, JsonElement.class);
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "pick ups config");
        boolean inverted;
        if (jsonObject.has("inverted")) {
            inverted = GsonHelper.getAsBoolean(jsonObject, "inverted");
        } else {
            inverted = false;
        }
        ResourceKey<Level> dimension;
        if (jsonObject.has("dimension")) {
            ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "dimension"));
            dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, resourceLocation);
        } else {
            dimension = null;
        }
        String[] items;
        if (jsonObject.has("items")) {
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "items");
            items = JsonConfigFileUtil.GSON.fromJson(jsonArray, String[].class);
        } else {
            items = new String[0];
        }
        DimensionBlacklist blacklist;
        if (dimension != null) {
            blacklist = dimensionBlacklists.computeIfAbsent(dimension, dimension1 -> DimensionBlacklist.create(inverted));
            if (inverted != blacklist.inverted()) throw new IllegalStateException("Found multiple configs for dimension %s where one is inverted and the other one is not, this is not allowed!".formatted(dimension));
        } else {
            if (inverted != defaultBlacklist.inverted()) {
                defaultBlacklist = DimensionBlacklist.create(inverted);
            }
            blacklist = defaultBlacklist;
        }
        blacklist.items().addAll(EntryCollectionBuilder.of(ForgeRegistries.ITEMS).buildSet(Arrays.asList(items)));
    }

    private record DimensionBlacklist(Set<Item> items, boolean inverted) {

        public static DimensionBlacklist create(boolean inverted) {
            return new DimensionBlacklist(Sets.newIdentityHashSet(), inverted);
        }
    }
}
