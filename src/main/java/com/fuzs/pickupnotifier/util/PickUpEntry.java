package com.fuzs.pickupnotifier.util;

import net.minecraft.item.Item;
import org.apache.commons.lang3.mutable.MutableFloat;

public class PickUpEntry {

    private final Item item;
    private final int count;
    private MutableFloat life;

    public PickUpEntry(Item stack, int count, int time) {
        this.item = stack;
        this.count = count;
        this.life = new MutableFloat(time);
    }

    public Item getItem() {
        return this.item;
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