package fuzs.pickupnotifier.client.handler;

import fuzs.pickupnotifier.client.gui.PositionPreset;
import fuzs.pickupnotifier.client.gui.entry.DisplayEntry;
import fuzs.pickupnotifier.client.util.PickUpCollector;
import fuzs.pickupnotifier.config.ConfigValueHolder;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.mutable.MutableInt;

public class DrawEntriesHandler {

    public static final PickUpCollector PICK_UPS = new PickUpCollector();
    public static final Int2ObjectArrayMap<MutableInt> HANDLED_ENTITIES = new Int2ObjectArrayMap<>();

    private final Minecraft mc = Minecraft.getInstance();
    private final int timeOutTicks = 80;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent evt) {

        if (evt.phase == TickEvent.Phase.END && !this.mc.isPaused()) {

            if (!HANDLED_ENTITIES.isEmpty()) {

                HANDLED_ENTITIES.values().forEach(MutableInt::increment);
                HANDLED_ENTITIES.values().removeIf(time -> time.intValue() > this.timeOutTicks);
            }

            if (ConfigValueHolder.getGeneralConfig().displayTime != 0 && !PICK_UPS.isEmpty()) {

                PICK_UPS.tick();
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlayText(RenderGameOverlayEvent.Text evt) {

        if (!PICK_UPS.isEmpty()) {

            final float scale = ConfigValueHolder.getDisplayConfig().scale / 6.0F;
            int scaledWidth = (int) (evt.getWindow().getGuiScaledWidth() / scale);
            int scaledHeight = (int) (evt.getWindow().getGuiScaledHeight() / scale);
            PositionPreset position = ConfigValueHolder.getDisplayConfig().position;
            int posX = (int) (ConfigValueHolder.getDisplayConfig().xOffset / scale);
            int posY = (int) (ConfigValueHolder.getDisplayConfig().yOffset / scale);
            int offset = position.getY(DisplayEntry.ENTRY_HEIGHT, scaledHeight, posY);
            int totalFade = ConfigValueHolder.getGeneralConfig().move ? (int) (PICK_UPS.getTotalFade(evt.getPartialTicks()) * DisplayEntry.ENTRY_HEIGHT) : 0;
            int entryX;
            int entryY = offset + (position.isBottom() ? totalFade : -totalFade);
            int entryHeight = position.isBottom() ? DisplayEntry.ENTRY_HEIGHT : -DisplayEntry.ENTRY_HEIGHT;

            for (DisplayEntry entry : PICK_UPS) {

                boolean mayRender = false;
                if (position.isBottom()) {

                    if (entryY < offset + entryHeight) {

                        mayRender = true;
                    }
                } else if (entryY > offset + entryHeight) {

                    mayRender = true;
                }

                if (mayRender) {

                    entryX = position.getX(entry.getEntryWidth(), scaledWidth, posX);
                    float alpha;
                    if (ConfigValueHolder.getGeneralConfig().move) {

                        alpha = Mth.clamp((float) (entryY - offset) / entryHeight, 0.0F, 1.0F);
                    } else {

                        alpha = entry.getRemainingTicksRelative(evt.getPartialTicks());
                    }

                    entry.render(evt.getMatrixStack(), entryX, entryY, alpha, scale);
                }

                entryY -= entryHeight;
            }
        }
    }

}
