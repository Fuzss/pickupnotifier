package fuzs.pickupnotifier.client.handler;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.gui.entry.DisplayEntry;
import fuzs.pickupnotifier.client.gui.entry.ExperienceDisplayEntry;
import fuzs.pickupnotifier.client.gui.entry.ItemDisplayEntry;
import fuzs.pickupnotifier.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AddEntriesHandler {

    public static void onEntityPickup(ClientLevel clientLevel, int entityId, int playerId, int amount) {
        // called by the client directly
        if (clientLevel.getEntity(playerId) instanceof LocalPlayer) {
            // prevent the client from adding duplicates already collected on the server-side
            if (!DrawEntriesHandler.INSTANCE.isItemEntityHandled(entityId)) {
                onEntityPickup(clientLevel, entityId, amount);
            }
        }
    }

    public static void addPickUpEntry(ClientLevel clientLevel, int entityId, int amount) {
        // called by package from server
        if (!PickUpNotifier.CONFIG.get(ClientConfig.class).general.forceClient) {
            // items collected on the server-side are added to this list to avoid creating duplicates when the client collects them as well
            DrawEntriesHandler.INSTANCE.addHandledEntity(entityId);
            onEntityPickup(clientLevel, entityId, amount);
        }
    }

    public static void addItemEntry(ItemStack itemStack) {
        // called by a packet sent from the server
        if (!PickUpNotifier.CONFIG.get(ClientConfig.class).general.forceClient
                && PickUpNotifier.CONFIG.get(ClientConfig.class).general.includeItems) {
            addItemEntry(itemStack, itemStack.getCount());
        }
    }

    private static void onEntityPickup(ClientLevel clientLevel, int itemId, int amount) {

        Entity entity = clientLevel.getEntity(itemId);
        if (entity instanceof ItemEntity item) {

            if (PickUpNotifier.CONFIG.get(ClientConfig.class).general.includeItems) {

                addItemEntry(item.getItem(), amount);
            }
        } else if (entity instanceof AbstractArrow abstractArrow) {

            if (PickUpNotifier.CONFIG.get(ClientConfig.class).general.includeArrows) {

                addItemEntry(abstractArrow.getPickupItemStackOrigin(), amount);
            }
        } else if (entity instanceof ExperienceOrb experience) {

            if (PickUpNotifier.CONFIG.get(ClientConfig.class).general.includeExperience) {

                addExperienceEntry(experience, amount);
            }
        }
    }

    private static void addItemEntry(@Nullable ItemStack itemStack, int amount) {

        // sometimes null, other mods do funny things
        if (itemStack != null && !itemStack.isEmpty()) {

            if (!PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.blacklist.contains(itemStack.getItem())) {

                addEntry(new ItemDisplayEntry(itemStack, amount));
            }
        }
    }

    private static void addExperienceEntry(ExperienceOrb orb, int amount) {

        if (orb.getValue() > 0) {

            if (PickUpNotifier.CONFIG.get(ClientConfig.class).general.experienceValue) {

                amount = orb.getValue();
            }

            addEntry(new ExperienceDisplayEntry(orb.getName(), amount));
        }
    }

    private static void addEntry(DisplayEntry displayEntry) {

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.getAbilities().instabuild && PickUpNotifier.CONFIG.get(
                ClientConfig.class).general.disableInCreative) {
            return;
        }

        int scaledHeight = (int) (minecraft.getWindow().getGuiScaledHeight()
                / (PickUpNotifier.CONFIG.get(ClientConfig.class).display.getScale()));
        int maxSize = (int) (scaledHeight * PickUpNotifier.CONFIG.get(ClientConfig.class).display.maxHeight
                / DisplayEntry.ENTRY_HEIGHT) - 1;

        ClientConfig.CombineEntries combineEntries = PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.combineEntries;
        Optional<DisplayEntry> duplicateDisplayEntry;
        if (combineEntries == ClientConfig.CombineEntries.NEVER) {
            duplicateDisplayEntry = Optional.empty();
        } else {
            duplicateDisplayEntry = DrawEntriesHandler.INSTANCE.getCollector()
                    .findDuplicate(displayEntry, combineEntries == ClientConfig.CombineEntries.EXCLUDE_NAMED);
        }

        if (duplicateDisplayEntry.isPresent()) {

            DisplayEntry duplicate = duplicateDisplayEntry.get();
            duplicate.mergeWith(displayEntry);
            DrawEntriesHandler.INSTANCE.getCollector().refresh(duplicate);
        } else {

            DrawEntriesHandler.INSTANCE.getCollector().add(displayEntry, maxSize);
        }
    }
}
