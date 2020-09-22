package com.fuzs.pickupnotifier.client.util;

import com.fuzs.pickupnotifier.client.gui.entry.DisplayEntry;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class PickUpCollector implements Iterable<DisplayEntry> {

    private final List<DisplayEntry> pickUps = Lists.newArrayList();

    public void tick(float partialTicks) {

        if (!this.pickUps.isEmpty()) {

            this.pickUps.forEach(pickUp -> pickUp.tick(partialTicks));
            this.pickUps.removeIf(DisplayEntry::isDead);
        }
    }

    public boolean isEmpty() {

        return this.pickUps.isEmpty();
    }

    public void refresh(DisplayEntry entry) {

        // adding back to the end of the list
        this.pickUps.remove(entry);
        this.pickUps.add(entry);
    }

    public void add(DisplayEntry entry, int maxSize) {

        if (this.pickUps.size() >= maxSize) {

            this.pickUps.remove(0);
        }

        this.pickUps.add(entry);
    }

    public Optional<DisplayEntry> findDuplicate(DisplayEntry entry) {

        return this.pickUps.stream().filter(pickUp -> pickUp.canMerge(entry)).findFirst();
    }

    public double getTotalFade() {

        return this.pickUps.stream().mapToDouble(DisplayEntry::getRelativeLife).average().orElse(0.0) * this.pickUps.size();
    }

    @Override
    @Nonnull
    public Iterator<DisplayEntry> iterator() {

        return this.pickUps.iterator();
    }

}
