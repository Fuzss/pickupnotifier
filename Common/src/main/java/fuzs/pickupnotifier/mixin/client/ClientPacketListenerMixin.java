package fuzs.pickupnotifier.mixin.client;

import fuzs.pickupnotifier.client.handler.AddEntriesHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin implements ClientGamePacketListener {

    @Inject(method = "handleTakeItemEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V", shift = At.Shift.AFTER))
    public void handleTakeItemEntity$invokeEnsureRunningOnSameThread(ClientboundTakeItemEntityPacket packet, CallbackInfo callback) {
        AddEntriesHandler.onEntityPickup(packet.getItemId(), packet.getPlayerId(), packet.getAmount());
    }
}
