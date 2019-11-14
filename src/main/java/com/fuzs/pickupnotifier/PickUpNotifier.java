package com.fuzs.pickupnotifier;

import com.fuzs.pickupnotifier.handler.ConfigBuildHandler;
import com.fuzs.pickupnotifier.handler.HudEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(PickUpNotifier.MODID)
public class PickUpNotifier {

    public static final String MODID = "pickupnotifier";
    public static final String NAME = "Pick Up Notifier";
    public static final Logger LOGGER = LogManager.getLogger(PickUpNotifier.NAME);

    public PickUpNotifier() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigBuildHandler.SPEC, MODID + ".toml");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {
        MinecraftForge.EVENT_BUS.register(new HudEventHandler());
    }

}
