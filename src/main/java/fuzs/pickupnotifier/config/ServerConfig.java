package fuzs.pickupnotifier.config;

import fuzs.pickupnotifier.config.core.AbstractConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig extends AbstractConfig.AbstractServerConfig {

    private final GeneralConfig general = new GeneralConfig();

    @Override
    protected void addToBuilder(ForgeConfigSpec.Builder builder) {

        setupConfig(this.general, builder);
    }

    public GeneralConfig general() {

        return this.general;
    }

    public static class GeneralConfig extends AbstractConfig {

        public boolean partialPickUps;
        public boolean backpackCompat;

        private GeneralConfig() {

            super("general");
        }

        @Override
        public void addToBuilder(ForgeConfigSpec.Builder builder) {

            register(builder.comment("Collect partial pick-up entries (when there isn't enough room in your inventory) in the log.", "Might accidentally log items that have not been picked up, therefore it can be disabled.").define("Partial Pick-Ups", true), v -> this.partialPickUps = v);
            register(builder.comment("Show entries for items picked up that don't go to the player's inventory. This will enable compatibility with some backpack mods, but might also falsely log items the player never actually receives.").define("Backpack Compatibility", false), v -> this.backpackCompat = v);
        }

    }

}
