package com.fuzs.pickupnotifier;

import com.fuzs.pickupnotifier.asm.hook.AddEntriesHook;
import com.fuzs.pickupnotifier.client.handler.DrawEntriesHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = PickUpNotifier.MODID,
        name = PickUpNotifier.NAME,
        version = PickUpNotifier.VERSION,
        acceptedMinecraftVersions = PickUpNotifier.RANGE,
        clientSideOnly = PickUpNotifier.CLIENT,
        certificateFingerprint = PickUpNotifier.FINGERPRINT
)
@Mod.EventBusSubscriber(modid = PickUpNotifier.MODID)
@SuppressWarnings({"WeakerAccess", "unused"})
public class PickUpNotifier {

    public static final String MODID = "pickupnotifier";
    public static final String NAME = "Pick Up Notifier";
    public static final String VERSION = "@VERSION@";
    public static final String RANGE = "[1.12.2]";
    public static final boolean CLIENT = true;
    public static final String FINGERPRINT = "@FINGERPRINT@";

    public static final Logger LOGGER = LogManager.getLogger(PickUpNotifier.NAME);

    @EventHandler
    public void onPostInit(FMLPostInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(new DrawEntriesHandler());
        AddEntriesHook.sync();
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent evt) {
        LOGGER.warn("Invalid fingerprint detected! The file " + evt.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent evt) {
        if (evt.getModID().equals(PickUpNotifier.MODID)) {
            ConfigManager.sync(PickUpNotifier.MODID, Config.Type.INSTANCE);
            AddEntriesHook.sync();
        }
    }

}
