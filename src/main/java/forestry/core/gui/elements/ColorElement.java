package forestry.core.gui.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class ColorElement extends GuiElement {

    private final int startColor;
    private final int endColor;


    public ColorElement(int width, int height, int startColor, int endColor) {
        super(width, height);
        this.startColor = startColor;
        this.endColor = endColor;
    }

    public ColorElement(int xPos, int yPos, int width, int height, int color) {
        super(xPos, yPos, width, height);
        this.startColor = color;
        this.endColor = color;
    }

    public ColorElement(int xPos, int yPos, int width, int height, int startColor, int endColor) {
        super(xPos, yPos, width, height);
        this.startColor = startColor;
        this.endColor = endColor;
    }

    @Override
    public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
        drawGradientRect(0, 0, width, height, startColor, endColor);
    }

    private void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(right, top, 0).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        buffer.pos(left, top, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos(left, bottom, 0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos(right, bottom, 0).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        tessellator.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }
}
