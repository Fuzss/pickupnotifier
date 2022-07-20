package fuzs.pickupnotifier.network.message;

import fuzs.pickupnotifier.client.handler.AddEntriesHandler;
import fuzs.puzzleslib.network.message.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class S2CTakeItemMessage implements Message {
    private int itemId;
    private int amount;

    public S2CTakeItemMessage() {

    }

    public S2CTakeItemMessage(int itemId, int amount) {
        this.itemId = itemId;
        this.amount = amount;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.itemId);
        buf.writeVarInt(this.amount);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.itemId = buf.readVarInt();
        this.amount = buf.readVarInt();
    }

    @Override
    public TakeItemHandler makeHandler() {
        return new TakeItemHandler();
    }

    private static class TakeItemHandler extends PacketHandler<S2CTakeItemMessage> {

        @Override
        public void handle(S2CTakeItemMessage packet, Player player, Object gameInstance) {
            AddEntriesHandler.addPickUpEntry(packet.itemId, packet.amount);
        }
    }
}
