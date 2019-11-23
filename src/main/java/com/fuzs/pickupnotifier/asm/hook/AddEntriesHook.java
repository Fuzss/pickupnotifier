package com.fuzs.pickupnotifier.asm.hook;

import com.fuzs.pickupnotifier.handler.ConfigBuildHandler;
import com.fuzs.pickupnotifier.util.DisplayEntry;
import com.fuzs.pickupnotifier.util.ExperienceDisplayEntry;
import com.fuzs.pickupnotifier.util.ItemDisplayEntry;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.List;
import java.util.Optional;

public class AddEntriesHook {

    public static final List<DisplayEntry> PICK_UPS = Lists.newArrayList();

    // accessed by asm transformer bundled with this mod
    @SuppressWarnings("unused")
    public static void onEntityPickup(Entity entity, EntityLivingBase livingentity) {

        if (livingentity instanceof EntityPlayerSP) {
            if (entity instanceof EntityItem) {
                addItemEntry(((EntityItem) entity).getItem());
            } else if (entity instanceof EntityXPOrb) {
                addExperienceEntry((EntityXPOrb) entity);
            }
        }

    }

    private static void addItemEntry(ItemStack stack) {

        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(stack.getItem());
        List<String> blacklist = Lists.newArrayList(ConfigBuildHandler.generalConfig.blacklist);
        boolean blacklisted = resourcelocation != null && (blacklist.contains(resourcelocation.toString())
                || blacklist.contains(resourcelocation.getResourceDomain()));

        if (!stack.isEmpty() && stack.getCount() > 0 && !blacklisted) {
            addEntry(new ItemDisplayEntry(stack));
        }

    }

    private static void addExperienceEntry(EntityXPOrb orb) {

        if (ConfigBuildHandler.generalConfig.displayExperience && orb.xpValue > 0) {
            addEntry(new ExperienceDisplayEntry(orb));
        }

    }

    private static void addEntry(DisplayEntry entry) {

        synchronized (PICK_UPS) {
            float scale = ConfigBuildHandler.displayConfig.scale / 6.0F;
            int scaledHeight = (int) (Minecraft.getMinecraft().displayHeight / scale);
            int length = (int) (scaledHeight * ConfigBuildHandler.displayConfig.height / DisplayEntry.HEIGHT) - 1;

            Optional<DisplayEntry> displayEntryOptional = ConfigBuildHandler.generalConfig.combineEntries ? PICK_UPS
                    .stream().filter(it -> it.canCombine(entry)).findFirst() : Optional.empty();
            if (displayEntryOptional.isPresent()) {
                DisplayEntry duplicateEntry = displayEntryOptional.get();
                duplicateEntry.addCount(entry.getCount());
                duplicateEntry.resetLife();
                // adding back to the end of the list
                PICK_UPS.remove(duplicateEntry);
                PICK_UPS.add(duplicateEntry);
            } else {
                if (PICK_UPS.size() >= length) {
                    PICK_UPS.remove(0);
                }
                PICK_UPS.add(entry);
            }
        }

    }

}
