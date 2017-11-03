package forestry.core.gui.elements;

import java.util.List;

import forestry.api.core.GuiElementAlignment;
import forestry.api.core.IGuiElement;

public class GuiElementWrapped extends GuiElement {
	private final IGuiElement element;
	private final GuiElementAlignment align;

	public GuiElementWrapped(int xPos, int yPos, int width, int height, IGuiElement element, GuiElementAlignment align) {
		super(xPos, yPos, width, height);
		this.element = element;
		this.align = align;
	}

	@Override
	public void draw(int startX, int startY) {

	}

	@Override
	public List<String> getToolTip(int mouseX, int mouseY) {
		return null;
	}

	public int getXOffset() {
		switch (align) {
			case RIGHT:
				return getWidth() - element.getWidth();
			case CENTER:
				return (getWidth() - element.getWidth()) / 2;
			default:
				return 0;
		}
	}

	public int getYOffset() {
		switch (align) {
			case RIGHT:
				return getHeight() - element.getHeight();
			case CENTER:
				return (getHeight() - element.getHeight()) / 2;
			default:
				return 0;
		}
	}
}
