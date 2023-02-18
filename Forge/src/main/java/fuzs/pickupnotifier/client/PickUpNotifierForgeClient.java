package fuzs.pickupnotifier.client;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.commands.ModReloadCommand;
import fuzs.pickupnotifier.client.handler.DrawEntriesHandler;
import fuzs.puzzleslib.client.core.ClientCoreServices;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = PickUpNotifier.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PickUpNotifierForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientCoreServices.FACTORIES.clientModConstructor(PickUpNotifier.MOD_ID).accept(new PickUpNotifierClient());
        registerHandlers();
    }

    private static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((final TickEvent.ClientTickEvent evt) -> {
            if (evt.phase == TickEvent.Phase.END) DrawEntriesHandler.INSTANCE.onClientTick(Minecraft.getInstance());
        });
        MinecraftForge.EVENT_BUS.addListener((final RegisterClientCommandsEvent evt) -> {
            ModReloadCommand.register(evt.getDispatcher(), (source, component) -> source.sendSuccess(component, true));
        });
    }

    @SubscribeEvent
    public static void onRegisterGuiOverlays(final RegisterGuiOverlaysEvent evt) {
        evt.registerAbove(VanillaGuiOverlay.DEBUG_TEXT.id(), "pick_up_notifications", (gui, poseStack, partialTick, width, height) -> {
            DrawEntriesHandler.INSTANCE.onRenderGameOverlayText(gui.getMinecraft(), poseStack, partialTick);
        });
    }
}
