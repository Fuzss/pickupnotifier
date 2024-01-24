package fuzs.pickupnotifier.client;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.commands.ModReloadCommand;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class PickUpNotifierFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(PickUpNotifier.MOD_ID, PickUpNotifierClient::new);
        registerHandlers();
    }

    private static void registerHandlers() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> ModReloadCommand.register(dispatcher, FabricClientCommandSource::sendFeedback));
    }
}
