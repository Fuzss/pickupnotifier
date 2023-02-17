package fuzs.pickupnotifier;

import fuzs.pickupnotifier.config.ClientConfig;
import fuzs.pickupnotifier.config.ServerConfig;
import fuzs.pickupnotifier.network.S2CTakeItemMessage;
import fuzs.pickupnotifier.network.S2CTakeItemStackMessage;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.core.CoreServices;
import fuzs.puzzleslib.core.ModConstructor;
import fuzs.puzzleslib.network.MessageDirection;
import fuzs.puzzleslib.network.NetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PickUpNotifier implements ModConstructor {
    public static final String MOD_ID = "pickupnotifier";
    public static final String MOD_NAME = "Pick Up Notifier";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandler NETWORK = CoreServices.FACTORIES.network(MOD_ID, true, true);
    @SuppressWarnings("Convert2MethodRef")
    public static final ConfigHolder CONFIG = CoreServices.FACTORIES
            .clientConfig(ClientConfig.class, () -> new ClientConfig())
            .serverConfig(ServerConfig.class, () -> new ServerConfig())
            .setFileName(ClientConfig.class, id -> ConfigHolder.moveToDir(id, ConfigHolder.defaultName(id, "client")))
            .setFileName(ServerConfig.class, id -> ConfigHolder.moveToDir(id, ConfigHolder.defaultName(id, "server")));

    @Override
    public void onConstructMod() {
        CONFIG.bakeConfigs(MOD_ID);
        registerMessages();
    }

    private static void registerMessages() {
        NETWORK.register(S2CTakeItemMessage.class, S2CTakeItemMessage::new, MessageDirection.TO_CLIENT);
        NETWORK.register(S2CTakeItemStackMessage.class, S2CTakeItemStackMessage::new, MessageDirection.TO_CLIENT);
    }
}
