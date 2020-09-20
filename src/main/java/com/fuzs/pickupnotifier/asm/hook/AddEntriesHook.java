package com.fuzs.pickupnotifier.asm.hook;

import com.fuzs.pickupnotifier.client.util.DisplayEntry;
import com.fuzs.pickupnotifier.client.util.ExperienceDisplayEntry;
import com.fuzs.pickupnotifier.client.util.ItemDisplayEntry;
import com.fuzs.pickupnotifier.config.ConfigBuildHandler;
import com.fuzs.pickupnotifier.config.EntryCollectionBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AddEntriesHook {

    public static final List<DisplayEntry> PICK_UPS = Lists.newArrayList();
    private static final Set<Item> BLACKLIST = Sets.newHashSet();

    // accessed by asm transformer bundled with this mod
    @SuppressWarnings("unused")
    public static void onEntityPickup(Entity entity, Entity livingentity) {

        if (livingentity == Minecraft.getMinecraft().player) {
            if (entity instanceof EntityItem) {
                addItemEntry(((EntityItem) entity).getItem());
            } else if (entity instanceof EntityTippedArrow) {
                addItemEntry(new ItemStack(Items.ARROW));
            } else if (entity instanceof EntitySpectralArrow) {
                addItemEntry(new ItemStack(Items.SPECTRAL_ARROW));
            } else if (entity instanceof EntityXPOrb) {
                addExperienceEntry((EntityXPOrb) entity);
            }
        }

    }

    private static void addItemEntry(ItemStack stack) {

        if (!stack.isEmpty() && stack.getCount() > 0 && !BLACKLIST.contains(stack.getItem())) {
            stack = stack.copy();
            addEntry(new ItemDisplayEntry(stack));
        }

    }

    private static void addExperienceEntry(EntityXPOrb orb) {

        if (ConfigBuildHandler.generalConfig.displayExperience && orb.xpValue > 0) {
            addEntry(new ExperienceDisplayEntry(orb));
        }

    }

    private static void addEntry(DisplayEntry entry) {

        float scale = ConfigBuildHandler.displayConfig.scale / 6.0F;
        int scaledHeight = (int) (new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() / scale);
        int length = (int) (scaledHeight * ConfigBuildHandler.displayConfig.height / DisplayEntry.HEIGHT) - 1;

        Optional<DisplayEntry> duplicateOptional = ConfigBuildHandler.generalConfig.combineEntries ? PICK_UPS
                .stream().filter(it -> it.canMerge(entry)).findFirst() : Optional.empty();
        if (duplicateOptional.isPresent()) {
            DisplayEntry duplicate = duplicateOptional.get();
            duplicate.merge(entry);
            // adding back to the end of the list
            PICK_UPS.remove(duplicate);
            PICK_UPS.add(duplicate);
        } else {
            if (PICK_UPS.size() >= length) {
                PICK_UPS.remove(0);
            }
            PICK_UPS.add(entry);
        }

    }

    public static void sync() {

        EntryCollectionBuilder<Item> builder = new EntryCollectionBuilder<>(ForgeRegistries.ITEMS);
        BLACKLIST.clear();
        List<String> blacklist = Lists.newArrayList(ConfigBuildHandler.generalConfig.blacklist);
        BLACKLIST.addAll(builder.buildEntrySet(blacklist));
    }

}
