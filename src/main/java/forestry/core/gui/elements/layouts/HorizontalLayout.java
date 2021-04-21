package forestry.core.gui.elements.layouts;

import forestry.core.gui.elements.GuiElement;

public class HorizontalLayout extends ElementLayout {

	public HorizontalLayout(int xPos, int yPos, int height) {
		super(xPos, yPos, 0, height);
	}

	@Override
	public <E extends GuiElement> E add(E element) {
		elements.add(element);
		element.setParent(this);
		element.setXPosition(width);
		setWidth(width + (element.getWidth() + distance));
		element.onCreation();
		return element;
	}

	@Override
	public <E extends GuiElement> E remove(E element) {
		elements.remove(element);
		setWidth(width - (element.getWidth() + distance));
		element.setXPosition(0);
		element.onDeletion();
		return element;
	}

	@Override
	public int getHeight() {
		if (height > 0) {
			return height;
		}
		int height = 0;
		for (GuiElement element : elements) {
			int elementHeight = element.getHeight();
			if (elementHeight > height) {
				height = elementHeight;
			}
		}
		return height;
	}
}
