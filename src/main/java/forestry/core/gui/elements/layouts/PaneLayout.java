package forestry.core.gui.elements.layouts;

import forestry.api.gui.IGuiElement;

public class PaneLayout extends ElementGroup {
	public PaneLayout(int width, int height) {
		super(0, 0, width, height);
	}

	public PaneLayout(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
	}

	@Override
	public int getWidth() {
		if (width > 0) {
			return width;
		}
		int width = 0;
		for (IGuiElement element : elements) {
			int elementWidth = element.getWidth();
			if (elementWidth > width) {
				width = elementWidth;
			}
		}
		return width;
	}

	@Override
	public int getHeight() {
		if (height > 0) {
			return height;
		}
		int height = 0;
		for (IGuiElement element : elements) {
			int elementHeight = element.getHeight();
			if (elementHeight > height) {
				height = elementHeight;
			}
		}
		return height;
	}
}
