package fuzs.pickupnotifier.network.message;

import fuzs.pickupnotifier.client.handler.AddEntriesHandler;
import fuzs.puzzleslib.network.message.Message;
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
    public PacketHandler<S2CTakeItemStackMessage> makeHandler() {
        return new TakeItemStackHandler();
    }

    private static class TakeItemStackHandler extends PacketHandler<S2CTakeItemStackMessage> {

        @Override
        public void handle(S2CTakeItemStackMessage packet, Player player, Object gameInstance) {
            AddEntriesHandler.addItemEntry(packet.stack);
        }
    }
}
