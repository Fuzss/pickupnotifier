package com.fuzs.pickupnotifier.asm;

import com.fuzs.pickupnotifier.client.handler.DrawEntriesHandler;
import com.fuzs.pickupnotifier.config.ConfigValueHolder;
import com.fuzs.pickupnotifier.client.gui.entry.DisplayEntry;
import com.fuzs.pickupnotifier.client.gui.entry.ExperienceDisplayEntry;
import com.fuzs.pickupnotifier.client.gui.entry.ItemDisplayEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Optional;

@SuppressWarnings("unused")
public class AddEntriesHandler {

    /**
     * accessed by asm transformer bundled with this mod
     */
    @SuppressWarnings("unused")
    public static void onEntityPickup(Entity entity, Entity livingentity) {

        if (livingentity == Minecraft.getInstance().player) {

            if (entity instanceof ItemEntity) {

                addItemEntry(((ItemEntity) entity).getItem());
            } else if (entity instanceof ArrowEntity) {

                addItemEntry(new ItemStack(Items.ARROW));
            } else if (entity instanceof SpectralArrowEntity) {

                addItemEntry(new ItemStack(Items.SPECTRAL_ARROW));
            } else if (entity instanceof TridentEntity) {

                addItemEntry(new ItemStack(Items.TRIDENT));
            } else if (entity instanceof ExperienceOrbEntity) {

                addExperienceEntry((ExperienceOrbEntity) entity);
            }
        }
    }

    private static void addItemEntry(ItemStack stack) {

        if (!stack.isEmpty() && stack.getCount() > 0 && !ConfigValueHolder.getGeneralConfig().blacklist.contains(stack.getItem())) {

            stack = stack.copy();
            // remove enchantments from copy as we don't want the glint to show
            stack.removeChildTag("Enchantments");
            addEntry(new ItemDisplayEntry(stack));
        }
    }

    private static void addExperienceEntry(ExperienceOrbEntity orb) {

        if (ConfigValueHolder.getGeneralConfig().displayExperience && orb.xpValue > 0) {

            addEntry(new ExperienceDisplayEntry(orb));
        }
    }

    private static void addEntry(DisplayEntry entry) {

        float scale = ConfigValueHolder.getDisplayConfig().scale / 6.0F;
        int scaledHeight = (int) (Minecraft.getInstance().getMainWindow().getScaledHeight() / scale);
        int maxSize = (int) (scaledHeight * ConfigValueHolder.getDisplayConfig().height / DisplayEntry.HEIGHT) - 1;

        Optional<DisplayEntry> duplicateOptional = ConfigValueHolder.getGeneralConfig().combineEntries ?
                DrawEntriesHandler.PICK_UPS.findDuplicate(entry) : Optional.empty();

        if (duplicateOptional.isPresent()) {

            DisplayEntry duplicate = duplicateOptional.get();
            duplicate.merge(entry);
            DrawEntriesHandler.PICK_UPS.refresh(duplicate);
        } else {

            DrawEntriesHandler.PICK_UPS.add(entry, maxSize);
        }
    }

}
