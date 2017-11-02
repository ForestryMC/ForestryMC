package forestry.core.gui.elements;

import forestry.api.core.IGuiElement;
import forestry.api.core.IGuiElementLayout;

public class GuiElementPanel extends GuiElementLayout {
	public GuiElementPanel(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
	}

	public IGuiElementLayout addElement(IGuiElement element) {
		elements.add(element);
		return this;
	}

	public IGuiElementLayout removeElement(IGuiElement element) {
		elements.remove(element);
		return this;
	}

	@Override
	public int getWidth() {
		if(width > 0){
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
