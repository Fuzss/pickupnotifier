package fuzs.pickupnotifier.network;

import fuzs.pickupnotifier.client.handler.AddEntriesHandler;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ClientboundTakeItemMessage(int itemId, int amount) implements ClientboundPlayMessage {
    public static final StreamCodec<ByteBuf, ClientboundTakeItemMessage> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT,
            ClientboundTakeItemMessage::itemId,
            ByteBufCodecs.VAR_INT,
            ClientboundTakeItemMessage::amount,
            ClientboundTakeItemMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                AddEntriesHandler.addPickUpEntry(context.client(),
                        ClientboundTakeItemMessage.this.itemId,
                        ClientboundTakeItemMessage.this.amount);
            }
        };
    }
}
