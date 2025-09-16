package fuzs.pickupnotifier.client.handler;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.gui.entry.DisplayEntry;
import fuzs.pickupnotifier.config.AnchorPoint;
import fuzs.pickupnotifier.config.ClientConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DrawEntriesHandler {
    public static final DrawEntriesHandler INSTANCE = new DrawEntriesHandler();
    private final Map<Object, DisplayEntry<?>> collector = new LinkedHashMap<>();
    private final Int2ObjectArrayMap<MutableInt> handledEntities = new Int2ObjectArrayMap<>();

    private DrawEntriesHandler() {
        // NO-OP
    }

    public void addHandledEntity(int itemId) {
        this.handledEntities.put(itemId, new MutableInt());
    }

    public boolean isItemEntityHandled(int itemId) {
        return this.handledEntities.containsKey(itemId);
    }

    public Map<Object, DisplayEntry<?>> getCollector() {
        return this.collector;
    }

    public void onClientTick(Minecraft minecraft) {
        if (minecraft.isPaused()) {
            return;
        }

        if (!PickUpNotifier.CONFIG.get(ClientConfig.class).general.clientOnly && !this.handledEntities.isEmpty()) {
            this.handledEntities.values().forEach(MutableInt::increment);
            this.handledEntities.values()
                    .removeIf((MutableInt mutableInt) -> mutableInt.intValue()
                            > PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.displayTime);
        }

        if (!this.collector.isEmpty()) {
            this.collector.values().forEach(DisplayEntry::tick);
            if (PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.displayTime != 0) {
                this.collector.values().removeIf(DisplayEntry::mayDiscard);
            }
        }
    }

    public void renderPickUpEntries(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (this.collector.isEmpty()) {
            return;
        }

        Font font = Minecraft.getInstance().font;
        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        float scale = PickUpNotifier.CONFIG.get(ClientConfig.class).display.getDisplayScale();
        int screenWidth = (int) (guiGraphics.guiWidth() / scale);
        int screenHeight = (int) (guiGraphics.guiHeight() / scale);

        AnchorPoint anchorPoint = PickUpNotifier.CONFIG.get(ClientConfig.class).display.position;
        Collection<DisplayEntry<?>> values = this.collector.values();
        if (!anchorPoint.isTop()) {
            values = new ArrayList<>(values).reversed();
        }

        int maxWidth = values.stream()
                .mapToInt((DisplayEntry<?> displayEntry) -> displayEntry.getEntryWidth(font))
                .max()
                .orElse(0);
        AnchorPoint.Positioner positioner = anchorPoint.createPositioner(screenWidth,
                screenHeight,
                maxWidth,
                DisplayEntry.ELEMENT_HEIGHT * values.size());

        int offsetX = (int) (PickUpNotifier.CONFIG.get(ClientConfig.class).display.offsetX / scale);
        int offsetY = (int) (PickUpNotifier.CONFIG.get(ClientConfig.class).display.offsetY / scale);
        ScreenRectangle originalRectangle = positioner.getRectangle(offsetX, offsetY);
        final int posX = positioner.getPosX(offsetX);
        final int posY = positioner.getPosY(offsetY - getMoveOffset(values, partialTick));
        int elementX;
        int elementY = posY;

        for (DisplayEntry<?> displayEntry : values) {
            int elementWidth = displayEntry.getEntryWidth(font);
            elementX = posX + anchorPoint.createPositioner(maxWidth, -1, elementWidth, -1).getPosX(0);
            if (PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.moveOut.move(ScreenAxis.HORIZONTAL)) {
                elementX += maxWidth * (1.0 - displayEntry.getRelativeRemainingTicks(partialTick))
                        * anchorPoint.getNormalX();
            }

            float alpha;
            if (!PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.fadeOut) {
                alpha = 1.0F;
            } else if (PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.moveOut.move(ScreenAxis.HORIZONTAL)
                    || PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.moveOut.move(ScreenAxis.VERTICAL)) {
                // we measure the area an entry has shifted outside the original display area to determine fade alpha
                alpha = getCoveredArea(elementX, elementY, elementWidth, originalRectangle);
            } else {
                alpha = displayEntry.getRelativeRemainingTicks(partialTick);
            }

            displayEntry.render(guiGraphics, font, elementX, elementY, alpha);
            elementY += DisplayEntry.ELEMENT_HEIGHT;
        }
    }

    private static int getMoveOffset(Collection<DisplayEntry<?>> values, float partialTick) {
        if (PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.moveOut.move(ScreenAxis.VERTICAL)) {
            return (int) (DisplayEntry.getRelativeRemainingTicks(values, partialTick) * DisplayEntry.ELEMENT_HEIGHT);
        } else {
            return 0;
        }
    }

    private static float getCoveredArea(int elementX, int elementY, int elementWidth, ScreenRectangle originalRectangle) {
        ScreenRectangle elementRectangle = new ScreenRectangle(elementX,
                elementY,
                elementWidth,
                DisplayEntry.ELEMENT_HEIGHT);
        ScreenRectangle screenRectangle = getScreenRectangleIntersection(originalRectangle, elementRectangle);
        int elementArea = elementRectangle.width() * elementRectangle.height();
        int intersectionArea = screenRectangle.width() * screenRectangle.height();
        return Mth.clamp(intersectionArea / (float) elementArea, 0.0F, 1.0F);
    }

    private static ScreenRectangle getScreenRectangleIntersection(ScreenRectangle originalRectangle, ScreenRectangle elementRectangle) {
        ScreenRectangle screenRectangle = originalRectangle.intersection(elementRectangle);
        if (screenRectangle != null) {
            return screenRectangle;
        } else {
            return ScreenRectangle.empty();
        }
    }

    public void onLoggedOut(LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection) {
        this.collector.clear();
    }

    public void onCopy(LocalPlayer oldPlayer, LocalPlayer newPlayer, MultiPlayerGameMode multiPlayerGameMode, Connection connection) {
        this.collector.clear();
    }
}
