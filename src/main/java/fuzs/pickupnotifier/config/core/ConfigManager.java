package fuzs.pickupnotifier.config.core;

import fuzs.pickupnotifier.PickUpNotifier;
import com.google.common.collect.Sets;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Set;
import java.util.function.Consumer;

public class ConfigManager {

    private static final Set<ConfigEntry<? extends ForgeConfigSpec.ConfigValue<?>, ?>> CONFIG_ENTRIES = Sets.newHashSet();

    private ConfigManager() {

    }

    public static void onModConfig(final ModConfigEvent.Reloading evt) {

        if (evt.getConfig().getModId().equals(PickUpNotifier.MODID)) {

            sync(evt.getConfig().getType());
        }
    }

    public static void sync() {

        CONFIG_ENTRIES.forEach(ConfigEntry::sync);
    }

    public static void sync(ModConfig.Type type) {

        CONFIG_ENTRIES.stream().filter(configValue -> configValue.getType() == type).forEach(ConfigEntry::sync);
    }

    public static <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerEntry(ModConfig.Type type, S entry, Consumer<T> action) {

        CONFIG_ENTRIES.add(new ConfigEntry<>(type, entry, action));
    }

    public static String getSimpleName(String modId) {

        return String.format("%s.toml", modId);
    }

    public static String getDefaultName(String modId, ModConfig.Type type) {

        return String.format("%s-%s.toml", modId, type.extension());
    }

    public static String getNameInModDir(String modId, @Nullable ModConfig.Type type) {

        return modId + File.separator + (type == null ? getSimpleName(modId) : getDefaultName(modId, type));
    }

    private static class ConfigEntry<S extends ForgeConfigSpec.ConfigValue<T>, T> {

        final ModConfig.Type type;
        final S entry;
        final Consumer<T> action;

        ConfigEntry(ModConfig.Type type, S entry, Consumer<T> action) {

            this.type = type;
            this.entry = entry;
            this.action = action;
        }

        ModConfig.Type getType() {

            return this.type;
        }

        void sync() {

            this.action.accept(this.entry.get());
        }

    }

}
