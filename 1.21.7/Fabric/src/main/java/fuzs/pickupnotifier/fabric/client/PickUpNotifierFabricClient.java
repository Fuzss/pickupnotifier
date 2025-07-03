package fuzs.pickupnotifier.fabric.client;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.PickUpNotifierClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class PickUpNotifierFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(PickUpNotifier.MOD_ID, PickUpNotifierClient::new);
    }
}
