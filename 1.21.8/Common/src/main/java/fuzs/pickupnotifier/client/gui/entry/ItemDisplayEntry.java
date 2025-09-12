package fuzs.pickupnotifier.client.gui.entry;

import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.client.util.DisplayEntryRenderHelper;
import fuzs.pickupnotifier.config.ClientConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemDisplayEntry extends DisplayEntry {
    static final int ITEM_STACK_POP_TIME = 5;

    private final ItemStack itemStack;

    public ItemDisplayEntry(ItemStack itemStack, int amount) {
        super(amount, itemStack.getRarity());
        this.itemStack = createDisplayItemStack(itemStack);
    }

    private static ItemStack createDisplayItemStack(ItemStack itemStack) {
        itemStack = itemStack.copy();
        // remove enchantments from copy as we don't want the glint to show
        if (PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.combineEntries
                == ClientConfig.CombineEntries.ALWAYS) {
            itemStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);
        }

        itemStack.setPopTime(ITEM_STACK_POP_TIME);
        return itemStack;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.itemStack.getPopTime() > 0) {
            this.itemStack.setPopTime(this.itemStack.getPopTime() - 1);
        }
    }

    @Override
    protected Component getEntryName() {
        if (PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.combineEntries
                == ClientConfig.CombineEntries.ALWAYS) {
            return this.itemStack.getItem().getName(this.itemStack);
        } else {
            return this.itemStack.getHoverName();
        }
    }

    @Override
    public boolean mayMergeWith(DisplayEntry other, boolean excludeNamed) {
        return other instanceof ItemDisplayEntry itemDisplayEntry && isSameItemSameName(this.itemStack,
                itemDisplayEntry.itemStack,
                excludeNamed);
    }

    @Override
    public void mergeWith(DisplayEntry other) {
        super.mergeWith(other);
        this.itemStack.setPopTime(ITEM_STACK_POP_TIME);
    }

    static boolean isSameItemSameName(ItemStack itemStack, ItemStack otherItemStack, boolean preferHoverName) {
        // TODO replace this with hashing and a linked hash map in favor of a normal list
        if (ItemStack.isSameItem(itemStack, otherItemStack)) {
            if (preferHoverName) {
                return itemStack.getHoverName().equals(otherItemStack.getHoverName()) && itemStack.getRarity()
                        .equals(otherItemStack.getRarity());
            } else {
                return itemStack.getItem().getName(itemStack).equals(otherItemStack.getItem().getName(otherItemStack));
            }
        } else {
            return false;
        }
    }

    @Override
    protected void appendTextComponents(List<Component> components, boolean reverse) {
        if (PickUpNotifier.CONFIG.get(ClientConfig.class).display.inventoryCount) {
            int inventoryCount = this.getInventoryCount(Minecraft.getInstance().player.getInventory());
            Component component = Component.literal(String.valueOf(inventoryCount));
            components.add(Component.literal("(").append(component).append(")"));
        }

        super.appendTextComponents(components, reverse);
    }

    private int getInventoryCount(Inventory inventory) {
        return ContainerHelper.clearOrCountMatchingItems(inventory,
                (ItemStack itemStack) -> ItemStack.isSameItem(this.itemStack, itemStack),
                0,
                true);
    }

    @Override
    protected void renderSprite(GuiGraphics guiGraphics, Font font, int posX, int posY, float fadeTime) {
        if (this.itemStack.getPopTime() > 0) {
            DeltaTracker deltaTracker = Minecraft.getInstance().getDeltaTracker();
            float popTime = this.itemStack.getPopTime() - deltaTracker.getGameTimeDeltaPartialTick(false);
            float popTimeScale = 1.0F + popTime / ITEM_STACK_POP_TIME;
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(posX + 8, posY + 12);
            guiGraphics.pose().scale(1.0F / popTimeScale, (popTimeScale + 1.0F) / 2.0F);
            guiGraphics.pose().translate(-(posX + 8), -(posY + 12));
        }

        DisplayEntryRenderHelper.renderItem(guiGraphics, this.itemStack, posX, posY, fadeTime);
        if (this.itemStack.getPopTime() > 0) {
            guiGraphics.pose().popMatrix();
        }

        if (PickUpNotifier.CONFIG.get(ClientConfig.class).display.displayAmount.isSprite()) {
            DisplayEntryRenderHelper.renderGuiItemDecorations(guiGraphics,
                    font,
                    this.getDisplayAmount(),
                    posX,
                    posY,
                    fadeTime);
        }
    }
}
