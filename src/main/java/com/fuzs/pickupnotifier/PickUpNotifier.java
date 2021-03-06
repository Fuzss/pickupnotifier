package com.fuzs.pickupnotifier;

import com.fuzs.pickupnotifier.config.ConfigValueHolder;
import com.fuzs.pickupnotifier.client.handler.DrawEntriesHandler;
import com.fuzs.pickupnotifier.config.ConfigSyncManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(PickUpNotifier.MODID)
public class PickUpNotifier {

    public static final String MODID = "pickupnotifier";
    public static final String NAME = "Pick Up Notifier";
    public static final Logger LOGGER = LogManager.getLogger(PickUpNotifier.NAME);

    @SuppressWarnings("Convert2Lambda")
    public PickUpNotifier() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onLoadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ConfigSyncManager::onModConfig);

        // Forge doesn't like this being a lambda
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> new DistExecutor.SafeRunnable() {

            @Override
            public void run() {

                ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
                ConfigValueHolder.getGeneralConfig().setupConfig(builder);
                ConfigValueHolder.getDisplayConfig().setupConfig(builder);
                ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, builder.build());

                FMLJavaModLoadingContext.get().getModEventBus().addListener(PickUpNotifier.this::onClientSetup);
            }

        });

        // clientSideOnly = true
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {

        MinecraftForge.EVENT_BUS.register(new DrawEntriesHandler());
    }

    private void onLoadComplete(final FMLLoadCompleteEvent evt) {

        ConfigSyncManager.sync();
    }

}
