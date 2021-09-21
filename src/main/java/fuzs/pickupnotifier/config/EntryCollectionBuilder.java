package fuzs.pickupnotifier.config;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class EntryCollectionBuilder<T extends IForgeRegistryEntry<T>> extends StringListParser<T> {

    public EntryCollectionBuilder(IForgeRegistry<T> registry, Logger logger) {

        super(registry, logger);
    }

    /**
     * @param locations resource locations to build set from
     * @return entry set associated with given resource locations in active registry
     */
    public Set<T> buildEntrySet(List<String> locations) {

        return this.buildEntrySetWithCondition(locations, flag -> true, "");
    }

    /**
     * @param locations resource locations to build set from
     * @return entry map associated with given resource locations in active registry paired with a given double value
     */
    public Map<T, Double> buildEntryMap(List<String> locations) {

        return this.buildEntryMapWithCondition(locations, (entry, value) -> true, "");
    }

    /**
     * @param locations resource locations to build set from
     * @param condition condition need to match for an entry to be added to the set
     * @param message message to be logged when condition is not met
     * @return entry set associated with given resource locations in active registry
     */
    public Set<T> buildEntrySetWithCondition(List<String> locations, Predicate<T> condition, String message) {

        Set<T> set = Sets.newHashSet();
        for (String source : locations) {

            this.getEntryFromRegistry(source.trim()).forEach(entry -> {

                if (condition.test(entry)) {

                    if (this.checkOverwrite(set.contains(entry), source)) {

                        set.add(entry);
                    }
                } else {

                    this.logError(source, message);
                }
            });
        }

        return set;
    }

    /**
     * @param locations resource locations to build set from
     * @param condition condition need to match for an entry to be added to the map
     * @param message message to be logged when condition is not met
     * @return entry map associated with given resource locations in active registry paired with a given double value
     */
    public Map<T, Double> buildEntryMapWithCondition(List<String> locations, BiPredicate<T, Double> condition, String message) {

        Map<T, Double> map = Maps.newHashMap();
        for (String source : locations) {

            String[] s = Arrays.stream(source.split(",")).map(String::trim).toArray(String[]::new);
            if (s.length == 2) {

                List<T> entries = this.getEntryFromRegistry(s[0]);
                if (entries.isEmpty()) {

                    continue;
                }

                Optional<Double> size = Optional.empty();
                try {

                    size = Optional.of(Double.parseDouble(s[1]));
                } catch (NumberFormatException ignored) {

                    this.logError(source, "Invalid number format");
                }

                size.ifPresent(value -> entries.forEach(entry -> {

                    if (condition.test(entry, value)) {

                        if (this.checkOverwrite(map.containsKey(entry), entry.toString())) {

                            map.put(entry, value);
                        }
                    } else {

                        this.logError(source, message);
                    }
                }));
            } else {

                this.logError(source, "Insufficient number of arguments");
            }
        }

        return map;
    }

}
