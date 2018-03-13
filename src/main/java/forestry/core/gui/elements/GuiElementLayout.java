package forestry.core.gui.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import forestry.api.core.IGuiElement;
import forestry.api.core.IGuiElementLayout;

public abstract class GuiElementLayout extends GuiElement implements IGuiElementLayout {
	protected final List<IGuiElement> elements = new ArrayList<>();
	protected final List<String> tooltip = new ArrayList<>();

	/* The distance between the different elements of this group. */
	public int distance;

	public GuiElementLayout(int xPos, int yPos, int width, int height) {
		super(xPos, yPos, width, height);
	}

	public GuiElementLayout setDistance(int distance) {
		this.distance = distance;
		return this;
	}

	public void addTooltip(String line) {
		tooltip.add(line);
	}

	public List<String> getTooltip() {
		return tooltip;
	}

	@Override
	public int getDistance() {
		return distance;
	}

	public List<IGuiElement> getElements() {
		return elements;
	}

	@Override
	public int getSize() {
		return elements.size();
	}

	@Override
	public void draw(int startX, int startY) {
		elements.forEach(element -> element.draw(startX + getX(), startY + getY()));
	}

	@Override
	public List<String> getToolTip(int mouseX, int mouseY) {
		mouseX -= getX();
		mouseY -= getY();
		for (IGuiElement element : elements) {
			if (element.isMouseOver(mouseX, mouseY)) {
				List<String> toolTip = element.getToolTip(mouseX, mouseY);
				if (!toolTip.isEmpty()) {
					return toolTip;
				}
			}
		}
		if (!tooltip.isEmpty()) {
			return tooltip;
		}
		return Collections.emptyList();
	}
}
