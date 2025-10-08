package fuzs.pickupnotifier.client.gui.entry;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.pickupnotifier.PickUpNotifier;
import fuzs.pickupnotifier.config.ClientConfig;
import fuzs.pickupnotifier.config.CombineEntries;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.Rarity;

public final class ExperienceDisplayEntry extends DisplayEntry<Component> {
    private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = ResourceLocationHelper.withDefaultNamespace(
            "textures/entity/experience_orb.png");

    private int tickCount;

    public ExperienceDisplayEntry(Component name, int displayAmount, int tickCount) {
        super(name, displayAmount, Rarity.UNCOMMON);
        this.tickCount = tickCount;
    }

    @Override
    protected Component getEntryName(Component component) {
        return component;
    }

    @Override
    public void tick() {
        super.tick();
        this.tickCount++;
    }

    @Override
    public DisplayEntry<?> mergeWith(DisplayEntry<?> otherDisplayEntry) {
        return new ExperienceDisplayEntry(this.item,
                this.getDisplayAmount() + otherDisplayEntry.getDisplayAmount(),
                this.tickCount);
    }

    @Override
    protected void renderSprite(GuiGraphics guiGraphics, Font font, int posX, int posY, float alpha) {
        int textureOffset = getXpTexture(this.getDisplayAmount());
        int textureX = textureOffset % 4 * 16;
        int textureY = textureOffset / 4 * 16;
        int textureColor = getExperienceOrbColor(this.tickCount / 2.0F, ARGB.as8BitChannel(alpha));
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                EXPERIENCE_ORB_TEXTURES,
                posX,
                posY,
                textureX,
                textureY,
                16,
                16,
                16,
                16,
                64,
                64,
                textureColor);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (PickUpNotifier.CONFIG.get(ClientConfig.class).behavior.combineEntries
                != CombineEntries.NEVER) {
            return obj instanceof ExperienceDisplayEntry;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 31;
    }

    /**
     * @see net.minecraft.client.renderer.entity.ExperienceOrbRenderer#render(ExperienceOrbRenderState, PoseStack,
     *         MultiBufferSource, int)
     */
    private static int getExperienceOrbColor(float ageInTicks, int alpha) {
        int red = ARGB.as8BitChannel((Mth.sin(ageInTicks) + 1.0F) * 0.5F);
        int green = 255;
        int blue = ARGB.as8BitChannel((Mth.sin(ageInTicks + (Mth.PI * 4.0F / 3.0F)) + 1.0F) * 0.1F);
        return ARGB.color(alpha, red, green, blue);
    }

    /**
     * @see ExperienceOrb#getIcon()
     */
    private static int getXpTexture(int displayCount) {
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
        } else if (displayCount >= 3) {
            return 1;
        } else {
            return 0;
        }
    }
}
