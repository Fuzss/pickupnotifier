package fuzs.pickupnotifier.mixin.client;

import fuzs.pickupnotifier.client.util.TransparencyBuffer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {

    @Inject(method = "resizeDisplay", at = @At("RETURN"))
    private void resizeDisplay(CallbackInfo callback) {
        TransparencyBuffer.resizeDisplay();
    }
}
