package fuzs.pickupnotifier.client;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.commands.ModReloadCommand;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = PickUpNotifier.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PickUpNotifierForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(PickUpNotifier.MOD_ID, PickUpNotifierClient::new);
        registerHandlers();
    }

    private static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((final RegisterClientCommandsEvent evt) -> {
            ModReloadCommand.register(evt.getDispatcher(), (CommandSourceStack source, Component component) -> source.sendSuccess(() -> component, true));
        });
    }
}
