package fuzs.pickupnotifier;

import fuzs.pickupnotifier.config.ClientConfig;
import fuzs.pickupnotifier.config.ServerConfig;
import fuzs.pickupnotifier.handler.ItemPickupHandler;
import fuzs.pickupnotifier.network.message.S2CTakeItemMessage;
import fuzs.pickupnotifier.network.message.S2CTakeItemStackMessage;
import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.config.ConfigHolderImpl;
import fuzs.puzzleslib.network.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PickUpNotifier.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PickUpNotifier {
    public static final String MOD_ID = "pickupnotifier";
    public static final String MOD_NAME = "Pick Up Notifier";
    public static final Logger LOGGER = LogManager.getLogger(PickUpNotifier.MOD_NAME);

    public static final NetworkHandler NETWORK = NetworkHandler.of(MOD_ID, true, true);
    @SuppressWarnings("Convert2MethodRef")
    public static final ConfigHolder<ClientConfig, ServerConfig> CONFIG = ConfigHolder.of(() -> new ClientConfig(), () -> new ServerConfig());

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        // this mod is optional on the server, so mark this compatible
        PuzzlesLib.setSideOnly();
        ((ConfigHolderImpl<?, ?>) CONFIG).addConfigs(MOD_ID);
        registerHandler();
        registerMessages();
    }

    private static void registerHandler() {
        final ItemPickupHandler handler = new ItemPickupHandler();
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, handler::onEntityItemPickup1);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, true, handler::onEntityItemPickup2);
        MinecraftForge.EVENT_BUS.addListener(handler::onPlayerItemPickup);
    }

    private static void registerMessages() {
        NETWORK.register(S2CTakeItemMessage.class, S2CTakeItemMessage::new, NetworkDirection.PLAY_TO_CLIENT);
        NETWORK.register(S2CTakeItemStackMessage.class, S2CTakeItemStackMessage::new, NetworkDirection.PLAY_TO_CLIENT);
    }
}
