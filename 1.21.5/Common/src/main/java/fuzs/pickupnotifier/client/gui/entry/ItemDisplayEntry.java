package fuzs.pickupnotifier.client.gui.entry;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.util.DisplayEntryRenderHelper;
import fuzs.pickupnotifier.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class ItemDisplayEntry extends DisplayEntry {
    private final ItemStack itemStack;

    public ItemDisplayEntry(ItemStack itemStack, int amount) {
        super(amount, itemStack.getRarity());
        this.itemStack = itemStack;
    }

    @Override
    protected Component getEntryName() {
        if (PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.combineEntries ==
                ClientConfig.CombineEntries.ALWAYS) {
            return this.itemStack.getItem().getName(this.itemStack);
        } else {
            return this.itemStack.getHoverName();
        }
    }

    @Override
    public boolean mayMergeWith(DisplayEntry other, boolean excludeNamed) {
        return other instanceof ItemDisplayEntry itemDisplayEntry &&
                this.sameItem(itemDisplayEntry.itemStack, excludeNamed);
    }

    private boolean sameItem(ItemStack other, boolean testHoverName) {
        if (this.itemStack.getItem() == other.getItem()) {
            if (testHoverName) {
                return this.itemStack.getHoverName().equals(other.getHoverName()) &&
                        this.itemStack.getRarity().equals(other.getRarity());
            } else {
                return this.itemStack.getItem().getName(this.itemStack).equals(other.getItem().getName(other));
            }
        } else {
            return false;
        }
    }

    @Override
    protected int getInventoryCount(Inventory inventory) {
        return ContainerHelper.clearOrCountMatchingItems(inventory,
                stack -> this.sameItem(stack, false),
                Integer.MAX_VALUE,
                true);
    }

    @Override
    protected void renderSprite(Minecraft minecraft, GuiGraphics guiGraphics, int posX, int posY, float scale, float fadeTime) {
        guiGraphics.renderItem(this.itemStack, posX, posY);
        if (PickUpNotifier.CONFIG.get(ClientConfig.class).display.displayAmount.isSprite()) {
            DisplayEntryRenderHelper.renderGuiItemDecorations(guiGraphics,
                    minecraft.font,
                    this.getDisplayAmount(),
                    posX,
                    posY);
        }
    }
}
