package fuzs.pickupnotifier.client;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.handler.DrawEntriesHandler;
import fuzs.pickupnotifier.client.handler.ItemBlacklistManager;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerCopyCallback;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerNetworkEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiCallback;

public class PickUpNotifierClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ClientTickEvents.END.register(DrawEntriesHandler.INSTANCE::onClientTick);
        RenderGuiCallback.EVENT.register(DrawEntriesHandler.INSTANCE::onRenderGui);
        ClientPlayerNetworkEvents.LOGGED_OUT.register(DrawEntriesHandler.INSTANCE::onLoggedOut);
        ClientPlayerCopyCallback.EVENT.register(DrawEntriesHandler.INSTANCE::onCopy);
    }

    @Override
    public void onClientSetup() {
        ItemBlacklistManager.INSTANCE.loadAll(PickUpNotifier.MOD_ID);
    }
}
