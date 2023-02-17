package fuzs.pickupnotifier.client;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.handler.DrawEntriesHandler;
import fuzs.pickupnotifier.config.ItemBlacklistManager;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = PickUpNotifier.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PickUpNotifierClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        registerHandlers();
    }

    private static void registerHandlers() {
        final DrawEntriesHandler handler = new DrawEntriesHandler();
        MinecraftForge.EVENT_BUS.addListener(handler::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(handler::onRenderGameOverlayText);
        MinecraftForge.EVENT_BUS.addListener((final RegisterClientCommandsEvent evt) -> {
            evt.getDispatcher().register(Commands.literal(PickUpNotifier.MOD_ID).then(Commands.literal("reload").executes(source -> {
                ItemBlacklistManager.loadAll(PickUpNotifier.MOD_ID);
                source.getSource().sendSuccess(new TextComponent("Successfully reloaded %s dimension configs!".formatted(PickUpNotifier.MOD_NAME)), true);
                return 1;
            })));
        });
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent evt) {
        ItemBlacklistManager.loadAll(PickUpNotifier.MOD_ID);
    }
}
