package forestry.core.gui.elements;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import forestry.api.core.IGuiElement;
import forestry.core.gui.widgets.IScrollable;

public class GuiElementScrollable extends GuiElementVertical implements IScrollable {
	protected final List<IGuiElement> visibleElements = new ArrayList<>();
	protected final int sizeX;
	protected int elementOffset;

	public GuiElementScrollable(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width);
		this.sizeX = height;
	}

	public void updateVisibleElements(int offset) {
		visibleElements.clear();
		int height = 0;
		int widgetEnd = yPos + sizeX;
		for (int i = 0; i < elements.size(); i++) {
			IGuiElement element = elements.get(i);
			if (i < offset) {
				height += element.getHeight();
				continue;
			}
			elementOffset = -height;
			int elementHeight = element.getY() + element.getHeight() + elementOffset;
			if (elementHeight > widgetEnd) {
				continue;
			}
			visibleElements.add(element);
		}
	}

	public int getInvisibleElementCount() {
		int count = 0;
		int widgetEnd = yPos + sizeX;
		for (int i = 0; i < elements.size(); i++) {
			IGuiElement element = elements.get(i);
			int elementHeight = element.getY() + element.getHeight();
			if (elementHeight > widgetEnd) {
				count++;
			}
		}
		return count;
	}

	@Override
	public void draw(int startX, int startY) {
		for (IGuiElement element : visibleElements) {
			element.draw(startX + getX(), startY + getY() + elementOffset);
		}
	}

	@Override
	public List<String> getToolTip(int mouseX, int mouseY) {
		mouseY -= getY() + elementOffset;
		mouseX -= getX();
		for (IGuiElement element : visibleElements) {
			if (element.isMouseOver(mouseX, mouseY)) {
				List<String> toolTip = element.getToolTip(mouseX, mouseY);
				if (!toolTip.isEmpty()) {
					return toolTip;
				}
			}
		}
		return Collections.emptyList();
	}

	@Override
	public void onScroll(int value) {
		updateVisibleElements(value);
	}

	public void clear() {
		removeElements(Lists.newArrayList(elements));
	}

	@Override
	public boolean isFocused(int mouseX, int mouseY) {
		return isMouseOver(mouseX, mouseY);
	}

	@Override
	public int getHeight() {
		return sizeX;
	}
}
