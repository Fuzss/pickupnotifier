package fuzs.pickupnotifier.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.handler.ItemBlacklistManager;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ModReloadCommand {

    public static <T extends SharedSuggestionProvider> void register(CommandDispatcher<T> dispatcher, BiConsumer<T, Component> feedbackSender) {
        dispatcher.register(LiteralArgumentBuilder.<T>literal(PickUpNotifier.MOD_ID).then(LiteralArgumentBuilder.<T>literal("reload").executes(context -> {
            return reload(component -> feedbackSender.accept(context.getSource(), component));
        })));
    }

    private static int reload(Consumer<Component> feedbackSender) {
        ItemBlacklistManager.INSTANCE.loadAll(PickUpNotifier.MOD_ID);
        feedbackSender.accept(Component.literal("Successfully reloaded %s dimension configs!".formatted(PickUpNotifier.MOD_NAME)));
        return 1;
    }
}
