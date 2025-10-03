package fuzs.pickupnotifier.mixin.client;

import fuzs.pickupnotifier.client.gui.render.state.TransparentGuiItemRenderState;
import fuzs.pickupnotifier.client.util.DisplayEntryRenderHelper;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderState.class)
abstract class GuiRenderStateMixin {

    @Inject(method = "submitItem", at = @At("HEAD"))
    public void submitItem(GuiItemRenderState renderState, CallbackInfo callback) {
        float alpha = DisplayEntryRenderHelper.getItemStackAlpha();
        if (alpha != 1.0F) {
            TransparentGuiItemRenderState.class.cast(renderState).pickupnotifier$setAlpha(alpha);
        }
    }
}
