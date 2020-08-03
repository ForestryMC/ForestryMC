package forestry.core.gui.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.gui.Drawable;

public class DrawableElement extends GuiElement {
    /* Attributes - Final */
    private final Drawable drawable;

    public DrawableElement(Drawable drawable) {
        this(0, 0, drawable);
    }

    public DrawableElement(int xPos, int yPos, Drawable drawable) {
        super(xPos, yPos, drawable.uWidth, drawable.vHeight);
        this.drawable = drawable;
    }

    public DrawableElement(int xPos, int yPos, int width, int height, Drawable drawable) {
        super(xPos, yPos, width, height);
        this.drawable = drawable;
    }

    @Override
    public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
        drawable.draw(transform, 0, width, height, 0);
    }
}
