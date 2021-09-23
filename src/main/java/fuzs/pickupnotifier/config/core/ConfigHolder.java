package fuzs.pickupnotifier.config.core;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Supplier;

public class ConfigHolder<C extends AbstractConfig, S extends AbstractConfig> {

    private final C client;
    private final S server;

    public ConfigHolder(Supplier<C> client, Supplier<S> server) {

        this.client = FMLEnvironment.dist.isClient() ? client.get() : null;
        this.server = server.get();
    }

    public ForgeConfigSpec buildSpec() {

        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        if (this.client != null) {

            builder.comment("Config options for the logical client, e. g. rendering, user input.");
            this.client.setupConfig(builder);
        }

        builder.comment("Config options for the logical server, e. g. game logic, data storage, entity collisions.");
        this.server.setupConfig(builder);

        return builder.build();
    }

    public C client() {

        return this.client;
    }

    public S server() {

        return this.server;
    }

}
