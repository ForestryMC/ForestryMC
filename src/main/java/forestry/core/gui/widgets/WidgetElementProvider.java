package forestry.core.gui.widgets;

import javax.annotation.Nullable;

import java.util.List;

import forestry.core.gui.elements.GuiElementScrollable;
import forestry.core.gui.tooltips.ToolTip;

public class WidgetElementProvider extends Widget implements IScrollable {
	protected GuiElementScrollable scrollable;

	public WidgetElementProvider(WidgetManager manager, int xPos, int yPos, int width, int height, GuiElementScrollable scrollable) {
		super(manager, xPos, yPos);
		this.scrollable = scrollable;
		this.width = width;
		this.height = height;
	}

	@Override
	public void draw(int startX, int startY) {
		scrollable.draw(startX + xPos, startY + yPos);
	}

	@Nullable
	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		mouseX -= xPos;
		mouseY -= yPos;
		List<String> tooltip = scrollable.getToolTip(mouseX, mouseY);
		if (tooltip.isEmpty()) {
			return null;
		}
		ToolTip toolTip = new ToolTip();
		toolTip.add(tooltip);
		return toolTip;
	}

	@Override
	public boolean isFocused(int mouseX, int mouseY) {
		mouseX -= xPos;
		mouseY -= yPos;
		return scrollable.isFocused(mouseX, mouseY);
	}

	@Override
	public void onScroll(int value) {
		scrollable.onScroll(value);
	}
}
