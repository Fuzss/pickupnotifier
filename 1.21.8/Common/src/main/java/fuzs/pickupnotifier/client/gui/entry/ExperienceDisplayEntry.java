package fuzs.pickupnotifier.client.gui.entry;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Rarity;

public class ExperienceDisplayEntry extends DisplayEntry {
    private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = ResourceLocationHelper.withDefaultNamespace(
            "textures/entity/experience_orb.png");

    private final Component name;
    private int ageInTicks;

    public ExperienceDisplayEntry(Component name, int amount) {
        super(amount, Rarity.UNCOMMON);
        this.name = name;
    }

    @Override
    public void tick() {
        super.tick();
        this.ageInTicks++;
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
    protected void renderSprite(GuiGraphics guiGraphics, Font font, int posX, int posY, float scale, float fadeTime) {
        int textureOffset = getXpTexture(this.getDisplayAmount());
        int textureX = textureOffset % 4 * 16;
        int textureY = textureOffset / 4 * 16;
        int textureColor = getExperienceOrbColor(this.ageInTicks / 2.0F, ARGB.as8BitChannel(fadeTime));
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

    /**
     * @see net.minecraft.client.renderer.entity.ExperienceOrbRenderer#render(ExperienceOrbRenderState, PoseStack,
     *         MultiBufferSource, int)
     */
    static int getExperienceOrbColor(float ageInTicks, int alpha) {
        int red = ARGB.as8BitChannel((Mth.sin(ageInTicks) + 1.0F) * 0.5F);
        int green = 255;
        int blue = ARGB.as8BitChannel((Mth.sin(ageInTicks + (Mth.PI * 4.0F / 3.0F)) + 1.0F) * 0.1F);
        return ARGB.color(alpha, red, green, blue);
    }

    /**
     * @see ExperienceOrb#getIcon()
     */
    static int getXpTexture(int displayCount) {
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
