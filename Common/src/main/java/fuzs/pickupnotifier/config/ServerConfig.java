package fuzs.pickupnotifier.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig implements ConfigCore {
    @Config(description = {"Collect partial pick-up entries (when there isn't enough room in your inventory) in the log.", "Might accidentally log items that have not been picked up, therefore it can be disabled."})
    public boolean partialPickUps = true;
    public boolean backpackCompat;

    @Override
    public void addToBuilder(ForgeConfigSpec.Builder builder, ValueCallback callback) {
        if (ModLoaderEnvironment.INSTANCE.getModLoader().isForge()) {
            callback.accept(builder.comment("Show entries for items picked up that don't go to the player's inventory. This will enable compatibility with some backpack mods, but might also falsely log items the player never actually receives; depending on the backpack implementation.").define("backpack_compat", false), v -> this.backpackCompat = v);
        }
    }
}
