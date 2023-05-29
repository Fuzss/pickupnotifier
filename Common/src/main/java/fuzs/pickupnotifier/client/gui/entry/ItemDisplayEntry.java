package fuzs.pickupnotifier.client.gui.entry;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.util.DisplayEntryRenderHelper;
import fuzs.pickupnotifier.client.util.TransparencyBuffer;
import fuzs.pickupnotifier.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class ItemDisplayEntry extends DisplayEntry {
    private final ItemStack stack;

    public ItemDisplayEntry(ItemStack stack, int amount) {

        super(amount, stack.getRarity());
        this.stack = stack;
    }

    @Override
    protected Component getEntryName() {

        if (PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.combineEntries == ClientConfig.CombineEntries.ALWAYS) {

            return this.stack.getItem().getName(this.stack);
        }

        return this.stack.getHoverName();
    }

    @Override
    public boolean mayMergeWith(DisplayEntry other, boolean excludeNamed) {

        return other instanceof ItemDisplayEntry itemDisplayEntry && this.sameItem(itemDisplayEntry.stack, excludeNamed);
    }

    private boolean sameItem(ItemStack other, boolean testHoverName) {

        if (this.stack.getItem() == other.getItem()) {
            if (testHoverName) {
                return this.stack.getHoverName().equals(other.getHoverName()) && this.stack.getRarity().equals(other.getRarity());
            } else {
                return this.stack.getItem().getName(this.stack).equals(other.getItem().getName(other));
            }
        }
        return false;
    }

    @Override
    protected int getInventoryCount(Inventory inventory) {

        return ContainerHelper.clearOrCountMatchingItems(inventory, stack -> this.sameItem(stack, false), Integer.MAX_VALUE, true);
    }

    @Override
    protected void renderSprite(Minecraft minecraft, PoseStack poseStack, int posX, int posY, float scale, float fadeTime) {

        TransparencyBuffer.prepareExtraFramebuffer();
        minecraft.getItemRenderer().renderAndDecorateItem(poseStack, this.stack, posX, posY);
        if (PickUpNotifier.CONFIG.get(ClientConfig.class).display.displayAmount.sprite()) {

            DisplayEntryRenderHelper.renderGuiItemDecorations(poseStack, minecraft.font, this.getDisplayAmount(), posX, posY);
        }

        TransparencyBuffer.preInject(fadeTime);

        // Align the matrix stack
        poseStack.pushPose();
        poseStack.scale(1.0F / scale, 1.0F / scale, 1.0F);

        // Draw the framebuffer texture
        TransparencyBuffer.drawExtraFramebuffer(poseStack);
        poseStack.popPose();

        TransparencyBuffer.postInject();
    }
}
