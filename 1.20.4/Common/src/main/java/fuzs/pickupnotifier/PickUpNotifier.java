package fuzs.pickupnotifier;

import fuzs.pickupnotifier.config.ClientConfig;
import fuzs.pickupnotifier.config.ServerConfig;
import fuzs.pickupnotifier.network.S2CTakeItemMessage;
import fuzs.pickupnotifier.network.S2CTakeItemStackMessage;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.network.v2.MessageDirection;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PickUpNotifier implements ModConstructor {
    public static final String MOD_ID = "pickupnotifier";
    public static final String MOD_NAME = "Pick Up Notifier";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandlerV2 NETWORK = NetworkHandlerV2.build(MOD_ID, true, true);
    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).client(ClientConfig.class).server(ServerConfig.class)
            .setFileName(ClientConfig.class, id -> ConfigHolder.moveToDir(id, ConfigHolder.defaultName(id, "client")))
            .setFileName(ServerConfig.class, id -> ConfigHolder.moveToDir(id, ConfigHolder.defaultName(id, "server")));

    @Override
    public void onConstructMod() {
        registerMessages();
    }

    private static void registerMessages() {
        NETWORK.register(S2CTakeItemMessage.class, S2CTakeItemMessage::new, MessageDirection.TO_CLIENT);
        NETWORK.register(S2CTakeItemStackMessage.class, S2CTakeItemStackMessage::new, MessageDirection.TO_CLIENT);
    }
}
