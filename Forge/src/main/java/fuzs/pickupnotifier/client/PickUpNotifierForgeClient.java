package fuzs.pickupnotifier.client;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.handler.DrawEntriesHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = PickUpNotifier.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PickUpNotifierForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        registerHandlers();
    }

    private static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent evt) -> {
            if (evt.phase != TickEvent.Phase.END) return;
            DrawEntriesHandler.INSTANCE.onClientTick(Minecraft.getInstance());
        });
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent evt) {
        OverlayRegistry.registerOverlayAbove(ForgeIngameGui.HUD_TEXT_ELEMENT, PickUpNotifier.MOD_ID + ":pick_up_notifications", (gui, poseStack, partialTick, width, height) -> {
            DrawEntriesHandler.INSTANCE.onRenderGameOverlayText(poseStack, partialTick);
        });
    }
}
