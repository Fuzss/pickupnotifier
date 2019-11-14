package com.fuzs.pickupnotifier.util;

import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableFloat;

public class PickUpEntry {

    private final ItemStack itemStack;
    private final int count;
    private MutableFloat life;

    public PickUpEntry(ItemStack stack, int count, int time) {
        this.itemStack = stack;
        this.count = count;
        this.life = new MutableFloat(time);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public MutableFloat getLife() {
        return this.life;
    }

    public boolean isDead() {
        return this.life.compareTo(new MutableFloat(0.0F)) < 0;
    }

    public void tick(float f) {
        this.life.subtract(f);
    }

    public int getCount() {
        return this.count;
    }

}