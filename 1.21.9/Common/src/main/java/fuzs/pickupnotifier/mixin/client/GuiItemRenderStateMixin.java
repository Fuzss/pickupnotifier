package fuzs.pickupnotifier.mixin.client;

import fuzs.pickupnotifier.client.gui.render.state.TransparentGuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiItemRenderState.class)
abstract class GuiItemRenderStateMixin implements TransparentGuiItemRenderState {
    @Nullable
    private Float pickupnotifier$alpha;

    @Override
    public float pickupnotifier$getAlpha() {
        return this.pickupnotifier$alpha != null ? this.pickupnotifier$alpha : 1.0F;
    }

    @Override
    public void pickupnotifier$setAlpha(float alpha) {
        this.pickupnotifier$alpha = alpha;
    }
}
