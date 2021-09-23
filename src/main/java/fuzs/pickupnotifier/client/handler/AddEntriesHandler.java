package fuzs.pickupnotifier.client.handler;

import fuzs.pickupnotifier.client.gui.entry.DisplayEntry;
import fuzs.pickupnotifier.client.gui.entry.ExperienceDisplayEntry;
import fuzs.pickupnotifier.client.gui.entry.ItemDisplayEntry;
import fuzs.pickupnotifier.config.ConfigValueHolder;
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

        if (Minecraft.getInstance().level.getEntity(playerId) instanceof LocalPlayer) {

            onEntityPickup(itemId, amount);
        }
    }

    public static void onEntityPickup(int itemId, int amount) {

        if (DrawEntriesHandler.HANDLED_ENTITIES.put(itemId, new MutableInt()) == null) {

            Entity pickedEntity = Minecraft.getInstance().level.getEntity(itemId);
            if (pickedEntity instanceof ItemEntity) {

                addItemEntry(((ItemEntity) pickedEntity).getItem(), amount);
            } else if (pickedEntity instanceof AbstractArrow) {

                addItemEntry(((AbstractArrowAccessor) pickedEntity).callGetPickupItem(), amount);
            } else if (pickedEntity instanceof ExperienceOrb) {

                addExperienceEntry((ExperienceOrb) pickedEntity, amount);
            }
        }
    }

    public static void addItemEntry(ItemStack stack) {

        addItemEntry(stack, stack.getCount());
    }

    private static void addItemEntry(ItemStack stack, int amount) {

        if (!stack.isEmpty() && !ConfigValueHolder.getGeneralConfig().blacklist.contains(stack.getItem())) {

            stack = stack.copy();
            // remove enchantments from copy as we don't want the glint to show
            stack.removeTagKey("Enchantments");
            addEntry(new ItemDisplayEntry(stack, amount));
        }
    }

    private static void addExperienceEntry(ExperienceOrb orb, int amount) {

        if (ConfigValueHolder.getGeneralConfig().displayExperience && orb.getValue() > 0) {

            addEntry(new ExperienceDisplayEntry(orb.getName(), amount));
        }
    }

    private static void addEntry(DisplayEntry newEntry) {

        int scaledHeight = (int) (Minecraft.getInstance().getWindow().getGuiScaledHeight() / (ConfigValueHolder.getDisplayConfig().scale / 6.0F));
        int maxSize = (int) (scaledHeight * ConfigValueHolder.getDisplayConfig().height / DisplayEntry.ENTRY_HEIGHT) - 1;
        Optional<DisplayEntry> possibleDuplicate = ConfigValueHolder.getGeneralConfig().combineEntries ? DrawEntriesHandler.PICK_UPS.findDuplicate(newEntry) : Optional.empty();
        if (possibleDuplicate.isPresent()) {

            DisplayEntry duplicate = possibleDuplicate.get();
            duplicate.mergeWith(newEntry);
            DrawEntriesHandler.PICK_UPS.refresh(duplicate);
        } else {

            DrawEntriesHandler.PICK_UPS.add(newEntry, maxSize);
        }
    }

}
