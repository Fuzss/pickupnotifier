package fuzs.pickupnotifier.client.handler;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.gui.PositionPreset;
import fuzs.pickupnotifier.client.gui.entry.DisplayEntry;
import fuzs.pickupnotifier.client.util.PickUpCollector;
import fuzs.pickupnotifier.config.ClientConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableInt;

public class DrawEntriesHandler {
    public static final DrawEntriesHandler INSTANCE = new DrawEntriesHandler();
    private final PickUpCollector collector = new PickUpCollector();
    private final Int2ObjectArrayMap<MutableInt> handledEntities = new Int2ObjectArrayMap<>();

    private DrawEntriesHandler() {

    }

    public void addHandledEntity(int itemId) {
        this.handledEntities.put(itemId, new MutableInt());
    }

    public boolean isItemEntityHandled(int itemId) {
        return this.handledEntities.containsKey(itemId);
    }

    public PickUpCollector getCollector() {
        return this.collector;
    }

    public void onClientTick(Minecraft minecraft) {

        if (minecraft.isPaused()) return;

        if (!PickUpNotifier.CONFIG.get(ClientConfig.class).general.forceClient && !this.handledEntities.isEmpty()) {

            this.handledEntities.values().forEach(MutableInt::increment);
            this.handledEntities.values().removeIf(time -> time.intValue() > 80);
        }

        if (PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.displayTime != 0 && !this.collector.isEmpty()) {

            this.collector.tick();
        }
    }

    public void onRenderGameOverlayText(PoseStack poseStack, float tickDelta) {

        if (this.collector.isEmpty()) return;

        final float scale = PickUpNotifier.CONFIG.get(ClientConfig.class).display.scale / 6.0F;
        Window window = Minecraft.getInstance().getWindow();
        int scaledWidth = (int) (window.getGuiScaledWidth() / scale);
        int scaledHeight = (int) (window.getGuiScaledHeight() / scale);
        PositionPreset position = PickUpNotifier.CONFIG.get(ClientConfig.class).display.position;
        int posX = (int) (PickUpNotifier.CONFIG.get(ClientConfig.class).display.offsetX / scale);
        int posY = (int) (PickUpNotifier.CONFIG.get(ClientConfig.class).display.offsetY / scale);
        int offset = position.getY(DisplayEntry.ENTRY_HEIGHT, scaledHeight, posY);
        int totalFade = PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.move ? (int) (this.collector.getTotalFade(tickDelta) * DisplayEntry.ENTRY_HEIGHT) : 0;
        int entryX;
        int entryY = offset + (position.bottom() ? totalFade : -totalFade);
        int entryHeight = position.bottom() ? DisplayEntry.ENTRY_HEIGHT : -DisplayEntry.ENTRY_HEIGHT;

        for (DisplayEntry entry : this.collector) {

            boolean mayRender = false;
            if (position.bottom()) {

                if (entryY < offset + entryHeight) {

                    mayRender = true;
                }
            } else if (entryY > offset + entryHeight) {

                mayRender = true;
            }

            if (mayRender) {

                entryX = position.getX(entry.getEntryWidth(), scaledWidth, posX);
                float alpha;
                if (PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.move) {

                    alpha = Mth.clamp((float) (entryY - offset) / entryHeight, 0.0F, 1.0F);
                } else {

                    alpha = entry.getRemainingTicksRelative(tickDelta);
                }

                entry.render(poseStack, entryX, entryY, alpha, scale);
            }

            entryY -= entryHeight;
        }
    }
}
