package fuzs.pickupnotifier;

import fuzs.pickupnotifier.handler.FabricItemPickupHandler;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.FabricPlayerEvents;
import net.fabricmc.api.ModInitializer;

public class PickUpNotifierFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(PickUpNotifier.MOD_ID, PickUpNotifier::new);
        registerHandler();
    }

    private static void registerHandler() {
        FabricPlayerEvents.ITEM_TOUCH.register(FabricItemPickupHandler::onEntityItemPickup);
    }
}
