package forestry.core.gui.elements.layouts;

import forestry.api.gui.IGuiElement;

public class HorizontalLayout extends AbstractElementLayout {

	public HorizontalLayout(int xPos, int yPos, int height) {
		super(xPos, yPos, 0, height);
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public <E extends IGuiElement> E add(E element) {
		elements.add(element);
		element.setParent(this);
		element.setXPosition(width);
		width += element.getWidth() + distance;
		return element;
	}

	public <E extends IGuiElement> E remove(E element) {
		elements.remove(element);
		width -= element.getWidth() + distance;
		element.setXPosition(0);
		return element;
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
