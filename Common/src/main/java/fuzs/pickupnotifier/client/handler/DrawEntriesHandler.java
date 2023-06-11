package fuzs.pickupnotifier.client.handler;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.gui.PositionPreset;
import fuzs.pickupnotifier.client.gui.entry.DisplayEntry;
import fuzs.pickupnotifier.client.util.PickUpCollector;
import fuzs.pickupnotifier.config.ClientConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
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

    public void onRenderGui(Minecraft minecraft, GuiGraphics guiGraphics, float tickDelta, int screenWidth, int screenHeight) {

        if (this.collector.isEmpty()) return;

        final float scale = PickUpNotifier.CONFIG.get(ClientConfig.class).display.scale / 6.0F;
        screenWidth /= scale;
        screenHeight /= scale;
        PositionPreset position = PickUpNotifier.CONFIG.get(ClientConfig.class).display.position;
        int posX = (int) (PickUpNotifier.CONFIG.get(ClientConfig.class).display.offsetX / scale);
        int posY = (int) (PickUpNotifier.CONFIG.get(ClientConfig.class).display.offsetY / scale);
        int offset = position.getY(DisplayEntry.ENTRY_HEIGHT, screenHeight, posY);
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

                entryX = position.getX(entry.getEntryWidth(minecraft), screenWidth, posX);
                float alpha;
                if (PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.move) {

                    alpha = Mth.clamp((float) (entryY - offset) / entryHeight, 0.0F, 1.0F);
                } else {

                    alpha = entry.getRemainingTicksRelative(tickDelta);
                }

                entry.render(minecraft, guiGraphics, entryX, entryY, alpha, scale);
            }

            entryY -= entryHeight;
        }
    }

    public void onLoggedOut(LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection) {

        this.collector.clear();
    }

    public void onCopy(LocalPlayer oldPlayer, LocalPlayer newPlayer, MultiPlayerGameMode multiPlayerGameMode, Connection connection) {

        this.collector.clear();
    }
}
