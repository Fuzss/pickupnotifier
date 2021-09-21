package fuzs.pickupnotifier.network;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.network.message.IMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * handler for network communications of all puzzles lib mods
 */
public enum NetworkHandler {

    INSTANCE;

    /**
     * protocol version for testing client-server compatibility of this mod
     */
    private final String protocolVersion = Integer.toString(1);
    /**
     * channel for sending messages
     */
    private final SimpleChannel channel = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(PickUpNotifier.MODID, "main"))
            .networkProtocolVersion(() -> this.protocolVersion)
            .clientAcceptedVersions(this.protocolVersion::equals)
            .serverAcceptedVersions(this.protocolVersion::equals)
            .simpleChannel();
    /**
     * message index
     */
    private final AtomicInteger discriminator = new AtomicInteger();

    /**
     * register a message for a side
     * @param clazz     message class type
     * @param supplier supplier for message (called when receiving at executing end)
     *                 we use this additional supplier to avoid having to invoke the class via reflection
     *                 and so that a default constructor in every message cannot be forgotten
     * @param direction side this message is to be executed at
     * @param <T> message implementation
     */
    public <T extends IMessage> void register(Class<T> clazz, Supplier<T> supplier, NetworkDirection direction) {

        BiConsumer<T, FriendlyByteBuf> encode = IMessage::write;
        Function<FriendlyByteBuf, T> decode = (buf) -> {

            T message = supplier.get();
            message.read(buf);
            return message;
        };
        BiConsumer<T, Supplier<NetworkEvent.Context>> handle = (message, contextSupplier) -> {

            NetworkEvent.Context ctx = contextSupplier.get();
            if (ctx.getDirection() == direction) {

                ctx.setPacketHandled(message.handle(ctx));
            } else {

                PickUpNotifier.LOGGER.warn("Received message {} at wrong side, was {}, expected {}", message.getClass().getSimpleName(), ctx.getDirection(), direction);
                ctx.setPacketHandled(true);
            }
        };

        this.channel.registerMessage(this.discriminator.getAndIncrement(), clazz, encode, decode, handle);
    }

    /**
     * send message from client to server
     * @param message message to send
     */
    public void sendToServer(IMessage message) {

        channel.sendToServer(message);
    }

    /**
     * send message from server to client
     * @param message message to send
     * @param player client player to send to
     */
    public void sendTo(IMessage message, ServerPlayer player) {

        channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    /**
     * send message from server to all clients
     * @param message message to send
     */
    public void sendToAll(IMessage message) {

        channel.send(PacketDistributor.ALL.noArg(), message);
    }

    /**
     * send message from server to all clients near given position
     * @param message message to send
     * @param level dimension key provider level
     * @param pos source position
     */
    public void sendToAllNear(IMessage message, Level level, BlockPos pos) {

        this.sendToAllNearExcept(message, level, pos, null);
    }

    /**
     * send message from server to all clients near given position
     * @param message message to send
     * @param level dimension key provider level
     * @param pos source position
     * @param exclude exclude player having caused this event
     */
    public void sendToAllNearExcept(IMessage message, Level level, BlockPos pos, @Nullable ServerPlayer exclude) {

        PacketDistributor.TargetPoint targetPoint = new PacketDistributor.TargetPoint(exclude, pos.getX(), pos.getY(), pos.getZ(), 64.0, level.dimension());
        channel.send(PacketDistributor.NEAR.with(() -> targetPoint), message);
    }

    /**
     * send message from server to all clients in dimension
     * @param message message to send
     * @param level dimension key provider level
     */
    public void sendToDimension(IMessage message, Level level) {

        this.sendToDimension(message, level.dimension());
    }

    /**
     * send message from server to all clients in dimension
     * @param message message to send
     * @param dimension dimension to send message in
     */
    public void sendToDimension(IMessage message, ResourceKey<Level> dimension) {

        channel.send(PacketDistributor.DIMENSION.with(() -> dimension), message);
    }

}
