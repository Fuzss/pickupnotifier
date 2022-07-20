package fuzs.pickupnotifier;

import fuzs.pickupnotifier.handler.ForgeItemPickupHandler;
import fuzs.puzzleslib.core.CoreServices;
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
        CoreServices.FACTORIES.modConstructor(PickUpNotifier.MOD_ID).accept(new PickUpNotifier());
        registerHandlers();
    }

    private static void registerHandlers() {
        final ForgeItemPickupHandler itemPickupHandler = new ForgeItemPickupHandler();
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, itemPickupHandler::onEntityItemPickup$1);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, true, itemPickupHandler::onEntityItemPickup$2);
        MinecraftForge.EVENT_BUS.addListener(itemPickupHandler::onPlayerItemPickup);
    }
}
