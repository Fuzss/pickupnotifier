package fuzs.pickupnotifier;

import fuzs.pickupnotifier.api.event.EntityItemPickupCallback;
import fuzs.pickupnotifier.handler.FabricItemPickupHandler;
import fuzs.puzzleslib.core.CoreServices;
import net.fabricmc.api.ModInitializer;

public class PickUpNotifierFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CoreServices.FACTORIES.modConstructor(PickUpNotifier.MOD_ID).accept(new PickUpNotifier());
        registerHandler();
    }

    private static void registerHandler() {
        final FabricItemPickupHandler itemPickupHandler = new FabricItemPickupHandler();
        EntityItemPickupCallback.EVENT.register(itemPickupHandler::onEntityItemPickup);
    }
}
