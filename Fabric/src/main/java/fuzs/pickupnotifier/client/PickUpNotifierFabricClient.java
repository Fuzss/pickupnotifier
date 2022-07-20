package fuzs.pickupnotifier.client;

import fuzs.pickupnotifier.client.handler.DrawEntriesHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class PickUpNotifierFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerHandlers();
    }

    private static void registerHandlers() {
        ClientTickEvents.END_CLIENT_TICK.register(DrawEntriesHandler.INSTANCE::onClientTick);
        HudRenderCallback.EVENT.register(DrawEntriesHandler.INSTANCE::onRenderGameOverlayText);
    }
}
