package fuzs.pickupnotifier.config.core;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.Consumer;

public abstract class AbstractConfig {

    private final String name;

    public AbstractConfig(String name) {

        this.name = name;
    }

    public final void setupConfig(ForgeConfigSpec.Builder builder) {

        setupConfig(this, builder);
    }

    protected abstract void addToBuilder(ForgeConfigSpec.Builder builder);

    protected static void setupConfig(AbstractConfig config, ForgeConfigSpec.Builder builder) {

        builder.push(config.name);
        config.addToBuilder(builder);
        builder.pop();
    }

    protected static <S extends ForgeConfigSpec.ConfigValue<T>, T> void register(S entry, Consumer<T> action) {

        ConfigManager.registerEntry(ModConfig.Type.COMMON, entry, action);
    }

    public static abstract class AbstractClientConfig extends AbstractConfig {

        public AbstractClientConfig() {

            super("client");
        }

    }

    public static abstract class AbstractServerConfig extends AbstractConfig {

        public AbstractServerConfig() {

            super("server");
        }

    }

}