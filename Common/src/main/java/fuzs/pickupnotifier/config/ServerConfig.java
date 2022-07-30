package fuzs.pickupnotifier.config;

import fuzs.puzzleslib.config.ConfigCore;
import fuzs.puzzleslib.config.annotation.Config;

public class ServerConfig implements ConfigCore {
    @Config(description = {"Collect partial pick-up entries (when there isn't enough room in your inventory) in the log.", "Might accidentally log items that have not been picked up, therefore it can be disabled."})
    public boolean partialPickUps = true;
    @Config(name = "backpack_compatibility", description = "Show entries for items picked up that don't go to the player's inventory. This will enable compatibility with some backpack mods, but might also falsely log items the player never actually receives.")
    public boolean backpackCompat = false;
}
