package fuzs.pickupnotifier.client;

import fuzs.pickupnotifier.client.handler.DrawEntriesHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerCopyCallback;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerNetworkEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiEvents;

public class PickUpNotifierClient implements ClientModConstructor {

    @Override
    public void onClientSetup() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ClientTickEvents.END.register(DrawEntriesHandler.INSTANCE::onClientTick);
        RenderGuiEvents.AFTER.register(DrawEntriesHandler.INSTANCE::onAfterRenderGui);
        ClientPlayerNetworkEvents.LOGGED_OUT.register(DrawEntriesHandler.INSTANCE::onLoggedOut);
        ClientPlayerCopyCallback.EVENT.register(DrawEntriesHandler.INSTANCE::onCopy);
    }
}
