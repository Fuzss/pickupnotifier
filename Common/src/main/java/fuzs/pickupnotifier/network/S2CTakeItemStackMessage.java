package fuzs.pickupnotifier.network;

import fuzs.pickupnotifier.client.handler.AddEntriesHandler;
import fuzs.puzzleslib.network.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class S2CTakeItemStackMessage implements Message<S2CTakeItemStackMessage> {
    private ItemStack stack;

    public S2CTakeItemStackMessage() {

    }

    public S2CTakeItemStackMessage(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeItem(this.stack);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.stack = buf.readItem();
    }

    @Override
    public MessageHandler<S2CTakeItemStackMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(S2CTakeItemStackMessage message, Player player, Object gameInstance) {
                AddEntriesHandler.addItemEntry(message.stack);
            }
        };
    }
}
