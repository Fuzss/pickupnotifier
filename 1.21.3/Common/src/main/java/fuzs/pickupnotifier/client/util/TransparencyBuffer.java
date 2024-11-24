package fuzs.pickupnotifier.client.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

/**
 * This class is used to render items with a given level of transparency.
 * <p>Copied from the <a href="https://github.com/samolego/ClientStorage">ClientStorage</a> mod by <a
 * href="https://github.com/samolego">samolego</a>.
 *
 * @see <a
 *         href="https://github.com/samolego/ClientStorage/blob/master/fabric-client/src/main/java/org/samo_lego/clientstorage/fabric_client/render/TransparencyBuffer.java">TransparencyBuffer.java</a>,
 *         licensed under <code>GNU Lesser General Public License v3.0</code>.
 */
public class TransparencyBuffer {
    private static final RenderTarget BUFFER_INSTANCE;
    private static int previousFramebuffer;

    static {
        Window window = Minecraft.getInstance().getWindow();
        BUFFER_INSTANCE = new TextureTarget(window.getWidth(), window.getHeight(), true);
        BUFFER_INSTANCE.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
    }

    public static void prepareExtraFramebuffer() {
        // Setup extra framebuffer to draw into
        previousFramebuffer = GlStateManager.getBoundFramebuffer();
        BUFFER_INSTANCE.clear();
        BUFFER_INSTANCE.bindWrite(false);
    }

    public static void drawExtraFramebuffer(GuiGraphics guiGraphics, float alphaValue) {

        RenderSystem.enableBlend();
        // Restore the original framebuffer
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousFramebuffer);

        // Render the custom framebuffer's contents with transparency into the main buffer
        RenderSystem.setShaderTexture(0, BUFFER_INSTANCE.getColorTextureId());
        Window window = Minecraft.getInstance().getWindow();
        // Create new matrix stack to prevent the transparency from affecting the rest of the GUI
        blit(guiGraphics.pose(), 0,                                       // x
                0,                                          // y
                window.getGuiScaledWidth(),                 // width
                window.getGuiScaledHeight(),                // height
                0,                                          // left-most coordinate of the texture region
                BUFFER_INSTANCE.height,            // top-most coordinate of the texture region
                BUFFER_INSTANCE.width,             // width of the texture region
                -BUFFER_INSTANCE.height,           // height of the texture region
                BUFFER_INSTANCE.width,             // width of the entire texture
                BUFFER_INSTANCE.height,             // height of the entire texture
                ARGB.color(ARGB.as8BitChannel(alphaValue), -1));
    }

    private static void blit(PoseStack poseStack, int x, int y, int width, int height, int uWidth, int vHeight, float uOffset, float vOffset, int textureWidth, int textureHeight, int color) {
        innerBlit(poseStack.last().pose(), x,
                x + width, y,
                y + height,
                (uOffset + 0.0F) / (float) textureWidth,
                (uOffset + (float) uWidth) / (float) textureWidth,
                (vOffset + 0.0F) / (float) textureHeight, (vOffset + (float) vHeight) / (float) textureHeight, color);
    }

    private static void innerBlit(Matrix4f matrix, int x1, int x2, int y1, int y2, float minU, float maxU, float minV, float maxV, int color) {
        RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);
        BufferBuilder bufferBuilder = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.addVertex(matrix, x1, y1, 0.0F).setColor(color).setUv(minU, minV);
        bufferBuilder.addVertex(matrix, x1, y2, 0.0F).setColor(color).setUv(minU, maxV);
        bufferBuilder.addVertex(matrix, x2, y2, 0.0F).setColor(color).setUv(maxU, maxV);
        bufferBuilder.addVertex(matrix, x2, y1, 0.0F).setColor(color).setUv(maxU, minV);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    public static void resizeDisplay() {
        Window window = Minecraft.getInstance().getWindow();
        BUFFER_INSTANCE.resize(window.getWidth(), window.getHeight());
    }
}
