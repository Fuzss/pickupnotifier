package fuzs.pickupnotifier.neoforge.client;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.PickUpNotifierClient;
import fuzs.pickupnotifier.client.commands.ModReloadCommand;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod.EventBusSubscriber(modid = PickUpNotifier.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PickUpNotifierNeoForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(PickUpNotifier.MOD_ID, PickUpNotifierClient::new);
        registerHandlers();
    }

    private static void registerHandlers() {
        NeoForge.EVENT_BUS.addListener((final RegisterClientCommandsEvent evt) -> {
            ModReloadCommand.register(evt.getDispatcher(), (CommandSourceStack source, Component component) -> source.sendSuccess(() -> component, true));
        });
    }
}
