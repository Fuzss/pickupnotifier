package fuzs.pickupnotifier.client;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.handler.DrawEntriesHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = PickUpNotifier.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PickUpNotifierClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        final DrawEntriesHandler handler = new DrawEntriesHandler();
        MinecraftForge.EVENT_BUS.addListener(handler::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(handler::onRenderGameOverlayText);
    }
}
