package fuzs.pickupnotifier.client.util;

import fuzs.pickupnotifier.client.gui.entry.DisplayEntry;

import java.util.ArrayList;
import java.util.Optional;

public class PickUpCollector extends ArrayList<DisplayEntry> {

    public void tick() {

        if (!this.isEmpty()) {

            this.forEach(DisplayEntry::tick);
            this.removeIf(DisplayEntry::mayDiscard);
        }
    }

    public void refresh(DisplayEntry entry) {

        // adding back to the end of the list
        this.remove(entry);
        this.add(entry);
    }

    public void add(DisplayEntry entry, int maxSize) {

        if (this.size() >= maxSize) {

            this.remove(0);
        }

        this.add(entry);
    }

    public Optional<DisplayEntry> findDuplicate(DisplayEntry entry, boolean excludeNamed) {

        return this.stream().filter(pickUp -> pickUp.mayMergeWith(entry, excludeNamed)).findAny();
    }

    public double getTotalFade(float partialTicks) {

        return this.stream()
                .mapToDouble(entry -> entry.getRemainingTicksRelative(partialTicks))
                .average()
                .orElse(0.0) * this.size();
    }

}
