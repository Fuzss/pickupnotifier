package fuzs.pickupnotifier.fabric.client;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.PickUpNotifierClient;
import fuzs.pickupnotifier.client.commands.ModReloadCommand;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;

public class PickUpNotifierFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(PickUpNotifier.MOD_ID, PickUpNotifierClient::new);
        registerHandlers();
    }

    private static void registerHandlers() {
        ClientCommandRegistrationCallback.EVENT.register((CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) -> {
            ModReloadCommand.register(dispatcher, FabricClientCommandSource::sendFeedback);
        });
    }
}
