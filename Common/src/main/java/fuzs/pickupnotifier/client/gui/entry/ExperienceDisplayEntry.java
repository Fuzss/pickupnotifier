package fuzs.pickupnotifier.client.gui.entry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.pickupnotifier.client.util.TransparencyBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Rarity;

public class ExperienceDisplayEntry extends DisplayEntry {
    private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = new ResourceLocation("textures/entity/experience_orb.png");

    private final Component name;

    public ExperienceDisplayEntry(Component name, int amount) {

        super(amount, Rarity.UNCOMMON);
        this.name = name;
    }

    @Override
    protected Component getEntryName() {

        return this.name;
    }

    @Override
    public boolean mayMergeWith(DisplayEntry other, boolean excludeNamed) {

        return other instanceof ExperienceDisplayEntry;
    }

    @Override
    protected int getInventoryCount(Inventory inventory) {
        return 0;
    }

    @Override
    protected void renderSprite(Minecraft minecraft, PoseStack poseStack, int posX, int posY, float scale, float fadeTime) {

        int textureOffset = this.getXpTexture(this.getDisplayAmount());
        int x = textureOffset % 4 * 16;
        int y = textureOffset / 4 * 16;
        float color = (int) (System.currentTimeMillis() % 10E5 / 50) / 4.0F;
        float r = (Mth.sin(color) + 1.0F) * 0.5F;
        float g = 1.0F;
        float b = (Mth.sin(color + 4.1887903F) + 1.0F) * 0.1F;

        TransparencyBuffer.prepareExtraFramebuffer();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, EXPERIENCE_ORB_TEXTURES);
        RenderSystem.setShaderColor(r, g, b, 1.0F);
        GuiComponent.blit(poseStack, posX, posY, x, y, 16, 16, 64, 64);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        TransparencyBuffer.preInject(fadeTime);

        // Align the matrix stack
        poseStack.pushPose();
        poseStack.scale(1.0F / scale, 1.0F / scale, 1.0F);

        // Draw the framebuffer texture
        TransparencyBuffer.drawExtraFramebuffer(poseStack);
        poseStack.popPose();

        TransparencyBuffer.postInject();
    }

    /**
     * Copied from {@link ExperienceOrb#getIcon()} since we do not have access to an object instance.
     */
    private int getXpTexture(int displayCount) {

        if (displayCount >= 2477) {

            return 10;
        } else if (displayCount >= 1237) {

            return 9;
        } else if (displayCount >= 617) {

            return 8;
        } else if (displayCount >= 307) {

            return 7;
        } else if (displayCount >= 149) {

            return 6;
        } else if (displayCount >= 73) {

            return 5;
        } else if (displayCount >= 37) {

            return 4;
        } else if (displayCount >= 17) {

            return 3;
        } else if (displayCount >= 7) {

            return 2;
        } else {

            return displayCount >= 3 ? 1 : 0;
        }
    }
}
