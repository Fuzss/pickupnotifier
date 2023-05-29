package fuzs.pickupnotifier.client.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import org.lwjgl.opengl.GL30;

/**
 * This class is used to render items with a given level of transparency.
 * <p>Copied from the <a href="https://github.com/samolego/ClientStorage">ClientStorage</a> mod by <a href="https://github.com/samolego">samolego</a>.
 *
 * @see <a href="https://github.com/samolego/ClientStorage/blob/master/fabric-client/src/main/java/org/samo_lego/clientstorage/fabric_client/render/TransparencyBuffer.java">TransparencyBuffer.java</a>, licensed under <code>GNU Lesser General Public License v3.0</code>.
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

    public static void drawExtraFramebuffer(PoseStack matrices) {
        // Restore the original framebuffer
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousFramebuffer);

        // Render the custom framebuffer's contents with transparency into the main buffer
        RenderSystem.setShaderTexture(0, BUFFER_INSTANCE.getColorTextureId());
        Window window = Minecraft.getInstance().getWindow();
        // Create new matrix stack to prevent the transparency from affecting the rest of the GUI
        GuiComponent.blit(matrices,
                0,                                       // x
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

    public static void postInject() {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void resizeDisplay() {
        Window window = Minecraft.getInstance().getWindow();
        BUFFER_INSTANCE.resize(window.getWidth(), window.getHeight(), Minecraft.ON_OSX);
    }
}
