package fuzs.pickupnotifier.client.gui.entry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.pickupnotifier.config.ConfigValueHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ItemDisplayEntry extends DisplayEntry {

    private final ItemStack stack;

    public ItemDisplayEntry(ItemStack stack, int amount) {

        super(amount, stack.getRarity());
        this.stack = stack;
    }

    @Override
    protected Component getEntryName() {

        return ConfigValueHolder.getGeneralConfig().combineEntries ? this.getStackName() : this.stack.getHoverName();
    }

    private Component getStackName() {

        return this.stack.getItem().getName(this.stack);
    }

    @Override
    public boolean mayMergeWith(DisplayEntry other) {

        return other instanceof ItemDisplayEntry && this.stack.getItem() == ((ItemDisplayEntry) other).stack.getItem() && this.getStackName().equals(((ItemDisplayEntry) other).getStackName());
    }

    @Override
    protected void renderSprite(PoseStack poseStack, int posX, int posY, float scale) {

        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.scale(scale, scale, 1.0F);
        this.mc.getItemRenderer().renderAndDecorateItem(this.stack, posX, posY);
        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

}
