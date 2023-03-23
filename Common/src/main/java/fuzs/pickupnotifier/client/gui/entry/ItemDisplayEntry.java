package fuzs.pickupnotifier.client.gui.entry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.pickupnotifier.PickUpNotifier;
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

        return PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.combineEntries ? this.stack.getItem().getName(this.stack) : this.stack.getHoverName();
    }

    @Override
    public boolean mayMergeWith(DisplayEntry other) {

        return other instanceof ItemDisplayEntry itemDisplayEntry && this.sameItem(itemDisplayEntry.stack);
    }

    private boolean sameItem(ItemStack other) {

        return this.stack.getItem() == other.getItem() && this.stack.getItem().getName(this.stack).equals(other.getItem().getName(other));
    }

    @Override
    protected int getInventoryCount(Inventory inventory) {

        return ContainerHelper.clearOrCountMatchingItems(inventory, this::sameItem, Integer.MAX_VALUE, true);
    }

    @Override
    protected void renderSprite(Minecraft minecraft, PoseStack poseStack, int posX, int posY, float scale, float fadeTime) {

        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.scale(scale, scale, 1.0F);
        RenderSystem.applyModelViewMatrix();
        minecraft.getItemRenderer().renderAndDecorateItem(this.stack, posX, posY);

        if (PickUpNotifier.CONFIG.get(ClientConfig.class).display.displayAmount.sprite()) {

            DisplayEntryRenderHelper.renderGuiItemDecorations(minecraft.getItemRenderer(), minecraft.font, this.getDisplayAmount(), posX, posY);
        }

        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
