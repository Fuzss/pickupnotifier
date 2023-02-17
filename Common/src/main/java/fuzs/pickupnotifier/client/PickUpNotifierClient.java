package fuzs.pickupnotifier.client;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.data.ItemBlacklistManager;
import fuzs.puzzleslib.client.core.ClientModConstructor;

public class PickUpNotifierClient implements ClientModConstructor {

    @Override
    public void onClientSetup() {
        ItemBlacklistManager.INSTANCE.loadAll(PickUpNotifier.MOD_ID);
    }
}
