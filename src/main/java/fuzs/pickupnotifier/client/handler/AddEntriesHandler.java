package fuzs.pickupnotifier.client.handler;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.gui.entry.DisplayEntry;
import fuzs.pickupnotifier.client.gui.entry.ExperienceDisplayEntry;
import fuzs.pickupnotifier.client.gui.entry.ItemDisplayEntry;
import fuzs.pickupnotifier.mixin.client.accessor.AbstractArrowAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Optional;

public class AddEntriesHandler {

    public static void onEntityPickup(int itemId, int playerId, int amount) {

        // called by client directly
        if (Minecraft.getInstance().level.getEntity(playerId) instanceof LocalPlayer) {

            // prevent client from adding duplicates already collected on server side
            if (!DrawEntriesHandler.HANDLED_ENTITIES.containsKey(itemId)) {

                onEntityPickup(itemId, amount);
            }
        }
    }

    public static void addPickUpEntry(int itemId, int amount) {

        // called by package froms server
        if (!PickUpNotifier.CONFIG.client().general().forceClient) {

            // items collected on server side are added to this list to avoid creating duplicates when the client collects them as well
            DrawEntriesHandler.HANDLED_ENTITIES.put(itemId, new MutableInt());
            onEntityPickup(itemId, amount);
        }
    }

    public static void addItemEntry(ItemStack stack) {

        // called by package froms server
        if (!PickUpNotifier.CONFIG.client().general().forceClient && PickUpNotifier.CONFIG.client().general().logItems) {

            addItemEntry(stack, stack.getCount());
        }
    }

    private static void onEntityPickup(int itemId, int amount) {

        Entity pickedEntity = Minecraft.getInstance().level.getEntity(itemId);
        if (pickedEntity instanceof ItemEntity) {

            if (PickUpNotifier.CONFIG.client().general().logItems) {

                addItemEntry(((ItemEntity) pickedEntity).getItem(), amount);
            }
        } else if (pickedEntity instanceof AbstractArrow) {

            if (PickUpNotifier.CONFIG.client().general().logArrows) {

                addItemEntry(((AbstractArrowAccessor) pickedEntity).callGetPickupItem(), amount);
            }
        } else if (pickedEntity instanceof ExperienceOrb) {

            if (PickUpNotifier.CONFIG.client().general().logExperience) {

                addExperienceEntry((ExperienceOrb) pickedEntity, amount);
            }
        }
    }

    private static void addItemEntry(ItemStack stack, int amount) {

        if (!stack.isEmpty() && !PickUpNotifier.CONFIG.client().behavior().blacklist.contains(stack.getItem())) {

            stack = stack.copy();
            // remove enchantments from copy as we don't want the glint to show
            stack.removeTagKey("Enchantments");
            addEntry(new ItemDisplayEntry(stack, amount));
        }
    }

    private static void addExperienceEntry(ExperienceOrb orb, int amount) {

        if (orb.getValue() > 0) {

            addEntry(new ExperienceDisplayEntry(orb.getName(), amount));
        }
    }

    private static void addEntry(DisplayEntry newEntry) {

        int scaledHeight = (int) (Minecraft.getInstance().getWindow().getGuiScaledHeight() / (PickUpNotifier.CONFIG.client().display().scale / 6.0F));
        int maxSize = (int) (scaledHeight * PickUpNotifier.CONFIG.client().display().height / DisplayEntry.ENTRY_HEIGHT) - 1;
        Optional<DisplayEntry> possibleDuplicate = PickUpNotifier.CONFIG.client().behavior().combineEntries ? DrawEntriesHandler.PICK_UPS.findDuplicate(newEntry) : Optional.empty();
        if (possibleDuplicate.isPresent()) {

            DisplayEntry duplicate = possibleDuplicate.get();
            duplicate.mergeWith(newEntry);
            DrawEntriesHandler.PICK_UPS.refresh(duplicate);
        } else {

            DrawEntriesHandler.PICK_UPS.add(newEntry, maxSize);
        }
    }

}
