package forestry.core.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import forestry.core.gui.Drawable;

public class WidgetSlider extends Widget {
    private final Drawable texture;

    private int xOffset;
    private int yOffset;

    public WidgetSlider(WidgetManager manager, int xPos, int yPos, Drawable texture) {
        super(manager, xPos, yPos);
        this.texture = texture;
        this.width = texture.uWidth;
        this.height = texture.vHeight;
    }

    public void setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    @Override
    public void draw(MatrixStack transform, int startY, int startX) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        texture.draw(transform, startY + yPos + yOffset, startX + xPos + xOffset);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        int xPos = this.xPos + xOffset;
        int yPos = this.yPos + yOffset;
        return mouseX >= xPos && mouseX <= xPos + this.width && mouseY >= yPos && mouseY <= yPos + this.height;
    }
}
