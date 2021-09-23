package fuzs.pickupnotifier;

import fuzs.pickupnotifier.client.handler.DrawEntriesHandler;
import fuzs.pickupnotifier.config.ConfigManager;
import fuzs.pickupnotifier.config.ConfigHolder;
import fuzs.pickupnotifier.handler.ItemPickupHandler;
import fuzs.pickupnotifier.network.NetworkHandler;
import fuzs.pickupnotifier.network.message.S2CTakeItemMessage;
import fuzs.pickupnotifier.network.message.S2CTakeItemStackMessage;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(PickUpNotifier.MODID)
public class PickUpNotifier {

    public static final String MODID = "pickupnotifier";
    public static final String NAME = "Pick Up Notifier";
    public static final Logger LOGGER = LogManager.getLogger(PickUpNotifier.NAME);

    public PickUpNotifier() {

        this.addListeners(FMLJavaModLoadingContext.get().getModEventBus());
        this.buildConfig(ModLoadingContext.get());
    }

    private void addListeners(IEventBus bus) {

        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onLoadComplete);
        bus.addListener(ConfigManager::onModConfig);
    }

    private void buildConfig(ModLoadingContext ctx) {

        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        ConfigHolder.getGeneralConfig().setupConfig(builder);
        ConfigHolder.getBehaviorConfig().setupConfig(builder);
        ConfigHolder.getDisplayConfig().setupConfig(builder);
        ctx.registerConfig(ModConfig.Type.COMMON, builder.build());
    }

    private void onCommonSetup(final FMLCommonSetupEvent evt) {

        this.registerMessages();
        MinecraftForge.EVENT_BUS.register(new ItemPickupHandler());
    }

    private void registerMessages() {

        NetworkHandler.INSTANCE.register(S2CTakeItemMessage.class, S2CTakeItemMessage::new, NetworkDirection.PLAY_TO_CLIENT);
        NetworkHandler.INSTANCE.register(S2CTakeItemStackMessage.class, S2CTakeItemStackMessage::new, NetworkDirection.PLAY_TO_CLIENT);
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {

        MinecraftForge.EVENT_BUS.register(new DrawEntriesHandler());
    }

    private void onLoadComplete(final FMLLoadCompleteEvent evt) {

        ConfigManager.sync();
    }

}