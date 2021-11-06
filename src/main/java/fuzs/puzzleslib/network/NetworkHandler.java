package fuzs.puzzleslib.network;

import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.core.PuzzlesLibMod;
import fuzs.puzzleslib.network.message.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.Objects;
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
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    /**
     * channel for sending messages
     */
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(PuzzlesLibMod.MOD_ID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();
    /**
     * message index
     */
    private static final AtomicInteger DISCRIMINATOR = new AtomicInteger();

    /**
     * register a message for a side
     * mostly from AutoRegLib, thanks Vazkii!
     * @param clazz     message class type
     * @param supplier supplier for message (called when receiving at executing end)
     *                 we use this additional supplier to avoid having to invoke the class via reflection
     *                 and so that a default constructor in every message cannot be forgotten
     * @param direction side this message is to be executed at
     * @param <T> message implementation
     */
    public <T extends Message> void register(Class<T> clazz, Supplier<T> supplier, NetworkDirection direction) {
        BiConsumer<T, FriendlyByteBuf> encode = Message::write;
        Function<FriendlyByteBuf, T> decode = buf -> {
            T message = supplier.get();
            message.read(buf);
            return message;
        };
        BiConsumer<T, Supplier<NetworkEvent.Context>> handle = (msg, ctxSup) -> {
            NetworkEvent.Context ctx = ctxSup.get();
            final LogicalSide receptionSide = direction.getReceptionSide();
            if (ctx.getDirection().getReceptionSide() != receptionSide) {
                throw new IllegalStateException(String.format("Received message on wrong side, expected %s, was %s", receptionSide, ctx.getDirection().getReceptionSide()));
            }
            Player player;
            if (receptionSide.isClient()) {
                player = PuzzlesLib.PROXY.getClientPlayer();
            } else {
                player = ctx.getSender();
            }
            ctx.enqueueWork(() -> msg.handle(player, LogicalSidedProvider.INSTANCE.get(receptionSide)));
            ctx.setPacketHandled(true);
        };
        CHANNEL.registerMessage(DISCRIMINATOR.getAndIncrement(), clazz, encode, decode, handle);
    }

    /**
     * send message from client to server
     * @param message message to send
     */
    public void sendToServer(Message message) {
        Objects.requireNonNull(Minecraft.getInstance().getConnection(), "Cannot send packets when not in game!");
        Minecraft.getInstance().getConnection().send(toServerboundPacket(message));
    }

    /**
     * send message from server to client
     * @param message message to send
     * @param player client player to send to
     */
    public void sendTo(Message message, ServerPlayer player) {
        player.connection.send(toClientboundPacket(message));
    }

    /**
     * send message from server to all clients
     * @param message message to send
     */
    public void sendToAll(Message message) {
        PuzzlesLib.PROXY.getGameServer().getPlayerList().broadcastAll(toClientboundPacket(message));
    }

    /**
     * send message from server to all clients except one
     * @param message message to send
     * @param exclude client to exclude
     */
    public void sendToAllExcept(Message message, ServerPlayer exclude) {
        final Packet<?> packet = toClientboundPacket(message);
        for (ServerPlayer player : PuzzlesLib.PROXY.getGameServer().getPlayerList().getPlayers()) {
            if (player != exclude) {
                player.connection.send(packet);
            }
        }
    }

    /**
     * send message from server to all clients near given position
     * @param message message to send
     * @param pos source position
     * @param level dimension key provider level
     */
    public void sendToAllNear(Message message, BlockPos pos, Level level) {
        this.sendToAllNearExcept(message, null, pos.getX(), pos.getY(), pos.getZ(), 64.0, level);
    }

    /**
     * send message from server to all clients near given position
     * @param message message to send
     * @param exclude exclude player having caused this event
     * @param posX     source position x
     * @param posY     source position y
     * @param posZ     source position z
     * @param distance distance from source to receive message
     * @param level dimension key provider level
     */
    public void sendToAllNearExcept(Message message, @Nullable ServerPlayer exclude, double posX, double posY, double posZ, double distance, Level level) {
        PuzzlesLib.PROXY.getGameServer().getPlayerList().broadcast(exclude, posX, posY, posZ, distance, level.dimension(), toClientboundPacket(message));
    }

    /**
     * send message from server to all clients in dimension
     * @param message message to send
     * @param level dimension key provider level
     */
    public void sendToDimension(Message message, Level level) {
        this.sendToDimension(message, level.dimension());
    }

    /**
     * send message from server to all clients in dimension
     * @param message message to send
     * @param dimension dimension to send message in
     */
    public void sendToDimension(Message message, ResourceKey<Level> dimension) {
        PuzzlesLib.PROXY.getGameServer().getPlayerList().broadcastAll(toClientboundPacket(message), dimension);
    }

    /**
     * @param message message to create packet from
     * @return      packet for message
     */
    private static Packet<?> toServerboundPacket(Message message) {
        return CHANNEL.toVanillaPacket(message, NetworkDirection.PLAY_TO_SERVER);
    }

    /**
     * @param message message to create packet from
     * @return      packet for message
     */
    private static Packet<?> toClientboundPacket(Message message) {
        return CHANNEL.toVanillaPacket(message, NetworkDirection.PLAY_TO_CLIENT);
    }
}
