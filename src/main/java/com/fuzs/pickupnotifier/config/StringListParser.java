package com.fuzs.pickupnotifier.config;

import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StringListParser<T extends IForgeRegistryEntry<T>> {

    private final IForgeRegistry<T> activeRegistry;
    private final Logger logger;
    
    protected StringListParser(IForgeRegistry<T> registry, Logger logger) {
        
        this.activeRegistry = registry;
        this.logger = logger;
    }

    protected final boolean checkOverwrite(boolean flag, String entry) {

        if (flag) {

            this.logError(entry, "Already present");
        }

        return !flag;
    }

    protected final List<T> getEntryFromRegistry(String source) {

        List<T> entries = Lists.newArrayList();
        Optional<ResourceLocation> location = Optional.ofNullable(ResourceLocation.tryCreate(source));
        if (location.isPresent()) {

            Optional<T> entry = this.getEntryFromRegistry(location.get());
            entry.ifPresent(entries::add);
        } else {

            this.getWildcardEntries(source, entries);
        }

        return entries;
    }

    private void getWildcardEntries(String source, List<T> entries) {

        String[] s = source.split(":");
        switch (s.length) {

            case 1:

                entries.addAll(this.getListFromRegistry("minecraft", s[0]));
                break;
            case 2:

                entries.addAll(this.getListFromRegistry(s[0], s[1]));
                break;
            default:

                this.logError(source, "Invalid resource location format");
        }
    }

    private Optional<T> getEntryFromRegistry(ResourceLocation location) {

        if (this.activeRegistry.containsKey(location)) {

            return Optional.ofNullable(this.activeRegistry.getValue(location));
        } else {

            this.logError(location.toString(), "Entry not found");
        }

        return Optional.empty();
    }

    private List<T> getListFromRegistry(String namespace, String path) {

        List<T> entries = this.activeRegistry.getEntries().stream()
                .filter(entry -> entry.getKey().func_240901_a_().getNamespace().equals(namespace))
                .filter(entry -> entry.getKey().func_240901_a_().getPath().matches(path.replace("*", "[a-z0-9/._-]*")))
                .map(Map.Entry::getValue).collect(Collectors.toList());

        if (entries.isEmpty()) {

            this.logError(namespace + ':' + path, "Entry not found");
        }

        return entries;
    }
    
    protected void logError(String entry, String message) {

        this.logger.error("Unable to parse entry \"{}\": {}", entry, message);
    }
    
}