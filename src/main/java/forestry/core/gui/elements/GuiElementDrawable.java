package forestry.core.gui.elements;

import forestry.core.gui.Drawable;

public class GuiElementDrawable extends GuiElement {
	private final Drawable drawable;

	public GuiElementDrawable(int xPos, int yPos, Drawable drawable) {
		super(xPos, yPos, drawable.width, drawable.height);
		this.drawable = drawable;
	}

	@Override
	public void draw(int startX, int startY) {
		drawable.draw(getX() + startX, getY() + startY);
	}
}
