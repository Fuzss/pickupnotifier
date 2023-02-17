package fuzs.pickupnotifier.client.handler;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.data.ItemBlacklistManager;
import fuzs.pickupnotifier.client.gui.entry.DisplayEntry;
import fuzs.pickupnotifier.client.gui.entry.ExperienceDisplayEntry;
import fuzs.pickupnotifier.client.gui.entry.ItemDisplayEntry;
import fuzs.pickupnotifier.config.ClientConfig;
import fuzs.pickupnotifier.mixin.client.accessor.AbstractArrowAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class AddEntriesHandler {

    public static void onEntityPickup(Minecraft minecraft, int entityId, int playerId, int amount) {

        // called by client directly
        if (Minecraft.getInstance().level.getEntity(playerId) instanceof LocalPlayer) {

            // prevent client from adding duplicates already collected on server side
            if (!DrawEntriesHandler.INSTANCE.isItemEntityHandled(entityId)) {

                onEntityPickup(minecraft, entityId, amount);
            }
        }
    }

    public static void addPickUpEntry(Minecraft minecraft, int entityId, int amount) {

        // called by package from server
        if (!PickUpNotifier.CONFIG.get(ClientConfig.class).general.forceClient) {

            // items collected on server side are added to this list to avoid creating duplicates when the client collects them as well
            DrawEntriesHandler.INSTANCE.addHandledEntity(entityId);
            onEntityPickup(minecraft, entityId, amount);
        }
    }

    public static void addItemEntry(Minecraft minecraft, ItemStack stack) {

        // called by package froms server
        if (!PickUpNotifier.CONFIG.get(ClientConfig.class).general.forceClient && PickUpNotifier.CONFIG.get(ClientConfig.class).general.logItems) {

            addItemEntry(minecraft, stack, stack.getCount());
        }
    }

    private static void onEntityPickup(Minecraft minecraft, int itemId, int amount) {

        Entity entity = minecraft.level.getEntity(itemId);
        if (entity instanceof ItemEntity item) {

            if (PickUpNotifier.CONFIG.get(ClientConfig.class).general.logItems) {

                addItemEntry(minecraft, item.getItem(), amount);
            }
        } else if (entity instanceof AbstractArrow) {

            if (PickUpNotifier.CONFIG.get(ClientConfig.class).general.logArrows) {

                addItemEntry(minecraft, ((AbstractArrowAccessor) entity).pickupnotifier$callGetPickupItem(), amount);
            }
        } else if (entity instanceof ExperienceOrb experience) {

            if (PickUpNotifier.CONFIG.get(ClientConfig.class).general.logExperience) {

                addExperienceEntry(minecraft, experience, amount);
            }
        }
    }

    private static void addItemEntry(Minecraft minecraft, ItemStack stack, int amount) {

        if (!stack.isEmpty() && ItemBlacklistManager.INSTANCE.isItemAllowed(minecraft.level.dimension(), stack.getItem())) {

            stack = stack.copy();
            // remove enchantments from copy as we don't want the glint to show
            stack.removeTagKey(ItemStack.TAG_ENCH);
            addEntry(minecraft, new ItemDisplayEntry(stack, amount));
        }
    }

    private static void addExperienceEntry(Minecraft minecraft, ExperienceOrb orb, int amount) {

        if (orb.getValue() > 0) {

            if (PickUpNotifier.CONFIG.get(ClientConfig.class).general.experienceValue) {

                amount = orb.getValue();
            }
            addEntry(minecraft, new ExperienceDisplayEntry(orb.getName(), amount));
        }
    }

    private static void addEntry(Minecraft minecraft, DisplayEntry newEntry) {

        int scaledHeight = (int) (minecraft.getWindow().getGuiScaledHeight() / (PickUpNotifier.CONFIG.get(ClientConfig.class).display.scale / 6.0F));
        int maxSize = (int) (scaledHeight * PickUpNotifier.CONFIG.get(ClientConfig.class).display.height / DisplayEntry.ENTRY_HEIGHT) - 1;
        Optional<DisplayEntry> possibleDuplicate = PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.combineEntries ? DrawEntriesHandler.INSTANCE.getCollector().findDuplicate(newEntry) : Optional.empty();
        if (possibleDuplicate.isPresent()) {

            DisplayEntry duplicate = possibleDuplicate.get();
            duplicate.mergeWith(newEntry);
            DrawEntriesHandler.INSTANCE.getCollector().refresh(duplicate);
        } else {

            DrawEntriesHandler.INSTANCE.getCollector().add(newEntry, maxSize);
        }
    }
}
