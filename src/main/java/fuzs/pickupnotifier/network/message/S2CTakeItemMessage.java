package fuzs.pickupnotifier.network.message;

import fuzs.pickupnotifier.client.handler.AddEntriesHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class S2CTakeItemMessage implements IMessage {

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
    public void handle(NetworkEvent.Context ctx) {

        ctx.enqueueWork(() -> AddEntriesHandler.onEntityPickup(this.itemId, this.amount));
    }

}
