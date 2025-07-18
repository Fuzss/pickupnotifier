package fuzs.pickupnotifier.client;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.handler.DrawEntriesHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerCopyCallback;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerNetworkEvents;

public class PickUpNotifierClient implements ClientModConstructor {

    @Override
    public void onClientSetup() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ClientTickEvents.END.register(DrawEntriesHandler.INSTANCE::onClientTick);
        ClientPlayerNetworkEvents.LOGGED_OUT.register(DrawEntriesHandler.INSTANCE::onLoggedOut);
        ClientPlayerCopyCallback.EVENT.register(DrawEntriesHandler.INSTANCE::onCopy);
    }

    @Override
    public void onRegisterGuiLayers(GuiLayersContext context) {
        context.registerGuiLayer(PickUpNotifier.id("pick_up_entries"),
                GuiLayersContext.DEBUG_OVERLAY,
                DrawEntriesHandler.INSTANCE::renderPickUpEntries);
    }
}
