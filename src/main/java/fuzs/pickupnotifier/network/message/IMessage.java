package fuzs.pickupnotifier.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

/**
 * network message template
 */
public interface IMessage {

    /**
     * writes message data to buffer
     * @param buf network data byte buffer
     */
    void write(final FriendlyByteBuf buf);

    /**
     * reads message data from buffer
     * @param buf network data byte buffer
     */
    void read(final FriendlyByteBuf buf);

    /**
     * handles message on receiving side
     * @param ctx message context for enqueuing work and player
     */
    void handle(NetworkEvent.Context ctx);

}
