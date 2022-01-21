package forestry.core.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;

import forestry.core.gui.Drawable;

public class DrawableElement extends GuiElement {
	/* Attributes - Final */
	private final Drawable drawable;

	public DrawableElement(Drawable drawable) {
		this(0, 0, drawable);
	}

	public DrawableElement(int xPos, int yPos, Drawable drawable) {
		super(xPos, yPos);
		setSize(drawable.uWidth, drawable.vHeight);
		this.drawable = drawable;
	}

	@Override
	public void drawElement(PoseStack transform, int mouseX, int mouseY) {
		if (bounds == null) {
			return;
		}
		drawable.draw(transform, 0, bounds.width, bounds.height, 0);
	}
}
