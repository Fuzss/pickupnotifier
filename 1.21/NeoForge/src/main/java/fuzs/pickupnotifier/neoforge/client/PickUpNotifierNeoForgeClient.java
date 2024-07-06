package fuzs.pickupnotifier.neoforge.client;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.PickUpNotifierClient;
import fuzs.pickupnotifier.client.commands.ModReloadCommand;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = PickUpNotifier.MOD_ID, dist = Dist.CLIENT)
public class PickUpNotifierNeoForgeClient {

    public PickUpNotifierNeoForgeClient(ModContainer modContainer) {
        ClientModConstructor.construct(PickUpNotifier.MOD_ID, PickUpNotifierClient::new);
        registerEventHandlers(NeoForge.EVENT_BUS);
    }

    private static void registerEventHandlers(IEventBus eventBus) {
        eventBus.addListener((final RegisterClientCommandsEvent evt) -> {
            ModReloadCommand.register(evt.getDispatcher(),
                    (CommandSourceStack source, Component component) -> source.sendSuccess(() -> component, true)
            );
        });
    }
}
