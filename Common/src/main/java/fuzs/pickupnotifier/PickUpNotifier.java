package fuzs.pickupnotifier;

import fuzs.pickupnotifier.config.ClientConfig;
import fuzs.pickupnotifier.config.ServerConfig;
import fuzs.pickupnotifier.network.message.S2CTakeItemMessage;
import fuzs.pickupnotifier.network.message.S2CTakeItemStackMessage;
import fuzs.puzzleslib.config.ConfigHolderV2;
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
    public static final ConfigHolderV2 CONFIG = CoreServices.FACTORIES
            .client(ClientConfig.class, () -> new ClientConfig())
            .server(ServerConfig.class, () -> new ServerConfig());

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
