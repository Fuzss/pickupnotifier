package fuzs.pickupnotifier.neoforge;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.neoforge.handler.NeoForgeItemPickupHandler;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(PickUpNotifier.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PickUpNotifierNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(PickUpNotifier.MOD_ID, PickUpNotifier::new);
        registerHandlers();
    }

    private static void registerHandlers() {
        // use native Forge events to be able to receive cancelled
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, NeoForgeItemPickupHandler::onEntityItemPickup$1);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, true, NeoForgeItemPickupHandler::onEntityItemPickup$2);
        NeoForge.EVENT_BUS.addListener(NeoForgeItemPickupHandler::onPlayerItemPickup);
    }
}
