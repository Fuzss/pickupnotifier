package fuzs.pickupnotifier.network;

import fuzs.pickupnotifier.client.handler.AddEntriesHandler;
import fuzs.puzzleslib.api.network.v2.WritableMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class S2CTakeItemStackMessage implements WritableMessage<S2CTakeItemStackMessage> {
    private final ItemStack stack;

    public S2CTakeItemStackMessage(ItemStack stack) {
        this.stack = stack;
    }

    public S2CTakeItemStackMessage(FriendlyByteBuf buf) {
        this.stack = buf.readItem();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeItem(this.stack);
    }

    @Override
    public MessageHandler<S2CTakeItemStackMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(S2CTakeItemStackMessage message, Player player, Object gameInstance) {
                AddEntriesHandler.addItemEntry((Minecraft) gameInstance, message.stack);
            }
        };
    }
}
