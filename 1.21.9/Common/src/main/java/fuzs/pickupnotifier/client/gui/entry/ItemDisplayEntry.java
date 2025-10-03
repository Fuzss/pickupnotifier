package fuzs.pickupnotifier.client.gui.entry;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.util.DisplayEntryRenderHelper;
import fuzs.pickupnotifier.config.ClientConfig;
import fuzs.pickupnotifier.config.CombineEntries;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public final class ItemDisplayEntry extends DisplayEntry<ItemStack> {
    private static final int ITEM_STACK_POP_TIME = 5;

    public ItemDisplayEntry(ItemStack itemStack, int displayAmount) {
        super(itemStack.copy(), displayAmount, itemStack.getRarity());
        this.item.setPopTime(ITEM_STACK_POP_TIME);
    }

    @Override
    protected Component getEntryName(ItemStack itemStack) {
        if (PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.combineEntries == CombineEntries.ALWAYS) {
            return itemStack.getItem().getName(itemStack);
        } else {
            return itemStack.getHoverName();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.item.getPopTime() > 0) {
            this.item.setPopTime(this.item.getPopTime() - 1);
        }
    }

    @Override
    public DisplayEntry<?> mergeWith(DisplayEntry<?> otherDisplayEntry) {
        return new ItemDisplayEntry(this.item, this.getDisplayAmount() + otherDisplayEntry.getDisplayAmount());
    }

    @Override
    protected void appendTextComponents(List<Component> components, boolean reverse) {
        if (PickUpNotifier.CONFIG.get(ClientConfig.class).display.inventoryCount) {
            Component inventoryCountComponent = Component.literal(String.valueOf(this.getInventoryCount()));
            components.add(Component.literal("(").append(inventoryCountComponent).append(")"));
        }

        super.appendTextComponents(components, reverse);
    }

    private int getInventoryCount() {
        Inventory inventory = Minecraft.getInstance().player.getInventory();
        return ContainerHelper.clearOrCountMatchingItems(inventory,
                (ItemStack itemStackX) -> ItemStack.isSameItem(this.item, itemStackX),
                0,
                true);
    }

    @Override
    protected void renderSprite(GuiGraphics guiGraphics, Font font, int posX, int posY, float alpha) {
        if (this.item.getPopTime() > 0) {
            DeltaTracker deltaTracker = Minecraft.getInstance().getDeltaTracker();
            float popTime = this.item.getPopTime() - deltaTracker.getGameTimeDeltaPartialTick(false);
            float popTimeScale = 1.0F + popTime / ITEM_STACK_POP_TIME;
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(posX + 8, posY + 12);
            guiGraphics.pose().scale(1.0F / popTimeScale, (popTimeScale + 1.0F) / 2.0F);
            guiGraphics.pose().translate(-(posX + 8), -(posY + 12));
        }

        DisplayEntryRenderHelper.renderItem(guiGraphics, this.item, posX, posY, alpha);
        if (this.item.getPopTime() > 0) {
            guiGraphics.pose().popMatrix();
        }

        if (PickUpNotifier.CONFIG.get(ClientConfig.class).display.displayAmount.isSprite()) {
            DisplayEntryRenderHelper.renderGuiItemDecorations(guiGraphics,
                    font,
                    this.getDisplayAmount(),
                    posX,
                    posY,
                    alpha);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.combineEntries != CombineEntries.NEVER
                && obj instanceof ItemDisplayEntry itemDisplayEntry) {
            if (ItemStack.isSameItem(this.item, itemDisplayEntry.item) && Objects.equals(this.item.getRarity(),
                    itemDisplayEntry.item.getRarity()) && Objects.equals(this.item.isEnchanted(),
                    itemDisplayEntry.item.isEnchanted())) {
                if (PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.combineEntries == CombineEntries.ALWAYS) {
                    return true;
                } else {
                    return Objects.equals(this.item.getHoverName(), itemDisplayEntry.item.getHoverName());
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.item.getItem().hashCode();
    }
}
