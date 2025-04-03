package fuzs.pickupnotifier.neoforge;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.neoforge.handler.NeoForgeItemPickupHandler;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(PickUpNotifier.MOD_ID)
public class PickUpNotifierNeoForge {

    public PickUpNotifierNeoForge(ModContainer modContainer) {
        ModConstructor.construct(PickUpNotifier.MOD_ID, PickUpNotifier::new);
        registerEventHandlers(NeoForge.EVENT_BUS);
    }

    private static void registerEventHandlers(IEventBus eventBus) {
        // use native Forge events to be able to receive cancelled
        eventBus.addListener(EventPriority.HIGH, NeoForgeItemPickupHandler::onEntityItemPickup$1);
        eventBus.addListener(EventPriority.LOW, true, NeoForgeItemPickupHandler::onEntityItemPickup$2);
        eventBus.addListener(NeoForgeItemPickupHandler::onPlayerItemPickup);
    }
}
