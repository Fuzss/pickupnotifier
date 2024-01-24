package fuzs.pickupnotifier;

import fuzs.pickupnotifier.handler.ForgeItemPickupHandler;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(PickUpNotifier.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PickUpNotifierForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(PickUpNotifier.MOD_ID, PickUpNotifier::new);
        registerHandlers();
    }

    private static void registerHandlers() {
        // use native Forge events to be able to receive cancelled
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, ForgeItemPickupHandler::onEntityItemPickup$1);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, true, ForgeItemPickupHandler::onEntityItemPickup$2);
        MinecraftForge.EVENT_BUS.addListener(ForgeItemPickupHandler::onPlayerItemPickup);
    }
}
