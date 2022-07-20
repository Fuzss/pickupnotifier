package fuzs.pickupnotifier.client.handler;

import fuzs.pickupnotifier.PickUpNotifier;
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

    public static void onEntityPickup(int itemId, int playerId, int amount) {

        // called by client directly
        if (Minecraft.getInstance().level.getEntity(playerId) instanceof LocalPlayer) {

            // prevent client from adding duplicates already collected on server side
            if (!DrawEntriesHandler.INSTANCE.isItemEntityHandled(itemId)) {

                onEntityPickup(itemId, amount);
            }
        }
    }

    public static void addPickUpEntry(int itemId, int amount) {

        // called by package froms server
        if (!PickUpNotifier.CONFIG.get(ClientConfig.class).general.forceClient) {

            // items collected on server side are added to this list to avoid creating duplicates when the client collects them as well
            DrawEntriesHandler.INSTANCE.addHandledEntity(itemId);
            onEntityPickup(itemId, amount);
        }
    }

    public static void addItemEntry(ItemStack stack) {

        // called by package froms server
        if (!PickUpNotifier.CONFIG.get(ClientConfig.class).general.forceClient && PickUpNotifier.CONFIG.get(ClientConfig.class).general.logItems) {

            addItemEntry(stack, stack.getCount());
        }
    }

    private static void onEntityPickup(int itemId, int amount) {

        Entity pickedEntity = Minecraft.getInstance().level.getEntity(itemId);
        if (pickedEntity instanceof ItemEntity) {

            if (PickUpNotifier.CONFIG.get(ClientConfig.class).general.logItems) {

                addItemEntry(((ItemEntity) pickedEntity).getItem(), amount);
            }
        } else if (pickedEntity instanceof AbstractArrow) {

            if (PickUpNotifier.CONFIG.get(ClientConfig.class).general.logArrows) {

                addItemEntry(((AbstractArrowAccessor) pickedEntity).callGetPickupItem(), amount);
            }
        } else if (pickedEntity instanceof ExperienceOrb) {

            if (PickUpNotifier.CONFIG.get(ClientConfig.class).general.logExperience) {

                addExperienceEntry((ExperienceOrb) pickedEntity, amount);
            }
        }
    }

    private static void addItemEntry(ItemStack stack, int amount) {

        if (!stack.isEmpty() && !PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.blacklist.contains(stack.getItem())) {

            stack = stack.copy();
            // remove enchantments from copy as we don't want the glint to show
            stack.removeTagKey("Enchantments");
            addEntry(new ItemDisplayEntry(stack, amount));
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

    private static void addEntry(DisplayEntry newEntry) {

        int scaledHeight = (int) (Minecraft.getInstance().getWindow().getGuiScaledHeight() / (PickUpNotifier.CONFIG.get(ClientConfig.class).display.scale / 6.0F));
        int maxSize = (int) (scaledHeight * PickUpNotifier.CONFIG.get(ClientConfig.class).display.height / DisplayEntry.ENTRY_HEIGHT) - 1;
        Optional<DisplayEntry> possibleDuplicate = PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.combineEntries ? DrawEntriesHandler.INSTANCE.getPickUpCollector().findDuplicate(newEntry) : Optional.empty();
        if (possibleDuplicate.isPresent()) {

            DisplayEntry duplicate = possibleDuplicate.get();
            duplicate.mergeWith(newEntry);
            DrawEntriesHandler.INSTANCE.getPickUpCollector().refresh(duplicate);
        } else {

            DrawEntriesHandler.INSTANCE.getPickUpCollector().add(newEntry, maxSize);
        }
    }
}
