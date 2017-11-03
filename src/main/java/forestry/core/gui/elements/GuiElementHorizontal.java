package forestry.core.gui.elements;

import forestry.api.core.IGuiElement;
import forestry.api.core.IGuiElementLayout;

public class GuiElementHorizontal extends GuiElementLayout {
	public GuiElementHorizontal(int xPos, int yPos, int height) {
		super(xPos, yPos, 0, height);
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public IGuiElementLayout addElement(IGuiElement element) {
		elements.add(element);
		element.setXOffset(width);
		width += element.getWidth() + distance;
		return this;
	}

	public IGuiElementLayout removeElement(IGuiElement element) {
		elements.remove(element);
		width -= element.getWidth() + distance;
		element.setXOffset(0);
		return this;
	}

	@Override
	public int getHeight() {
		if(height > 0){
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
