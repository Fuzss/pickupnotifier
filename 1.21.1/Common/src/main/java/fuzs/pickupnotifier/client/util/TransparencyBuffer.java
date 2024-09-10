package fuzs.pickupnotifier.client.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
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
        BUFFER_INSTANCE = new TextureTarget(window.getWidth(), window.getHeight(), true, Minecraft.ON_OSX);
        BUFFER_INSTANCE.setClearColor(0, 0, 0, 0);
    }

    public static void prepareExtraFramebuffer() {
        // Setup extra framebuffer to draw into
        previousFramebuffer = GlStateManager.getBoundFramebuffer();
        BUFFER_INSTANCE.clear(Minecraft.ON_OSX);
        BUFFER_INSTANCE.bindWrite(false);
    }

    public static void preInject(float alpha) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
    }

    public static void drawExtraFramebuffer(GuiGraphics guiGraphics) {
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
                BUFFER_INSTANCE.height             // height of the entire texture
        );
    }

    public static void blit(PoseStack poseStack, int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight) {
        blit(poseStack, x, x + width, y, y + height, 0, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
    }

    private static void blit(PoseStack poseStack, int i, int j, int k, int l, int m, int n, int o, float f, float g, int p, int q) {
        innerBlit(poseStack.last().pose(),
                i,
                j,
                k,
                l,
                m,
                (f + 0.0F) / (float) p,
                (f + (float) n) / (float) p,
                (g + 0.0F) / (float) q,
                (g + (float) o) / (float) q
        );
    }

    private static void innerBlit(Matrix4f matrix, int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix, (float) x1, (float) y1, (float) blitOffset).setUv(minU, minV);
        bufferBuilder.addVertex(matrix, (float) x1, (float) y2, (float) blitOffset).setUv(minU, maxV);
        bufferBuilder.addVertex(matrix, (float) x2, (float) y2, (float) blitOffset).setUv(maxU, maxV);
        bufferBuilder.addVertex(matrix, (float) x2, (float) y1, (float) blitOffset).setUv(maxU, minV);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    public static void postInject() {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void resizeDisplay() {
        Window window = Minecraft.getInstance().getWindow();
        BUFFER_INSTANCE.resize(window.getWidth(), window.getHeight(), Minecraft.ON_OSX);
    }
}
