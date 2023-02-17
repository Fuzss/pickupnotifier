package fuzs.pickupnotifier.client;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.commands.ModReloadCommand;
import fuzs.pickupnotifier.client.handler.DrawEntriesHandler;
import fuzs.puzzleslib.client.core.ClientCoreServices;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class PickUpNotifierFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCoreServices.FACTORIES.clientModConstructor(PickUpNotifier.MOD_ID).accept(new PickUpNotifierClient());
        registerHandlers();
    }

    private static void registerHandlers() {
        ClientTickEvents.END_CLIENT_TICK.register(DrawEntriesHandler.INSTANCE::onClientTick);
        HudRenderCallback.EVENT.register(DrawEntriesHandler.INSTANCE::onRenderGameOverlayText);
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> ModReloadCommand.register(dispatcher, FabricClientCommandSource::sendFeedback));
    }
}
