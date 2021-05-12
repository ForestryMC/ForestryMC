package forestry.core.gui.elements.layouts;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import forestry.core.gui.elements.GuiElement;

import genetics.Log;

public class FreeLayout implements Layout {
	public static final FreeLayout INSTANCE = new FreeLayout();

	protected FreeLayout() {
	}

	@Override
	public void layoutContainer(Rectangle bounds, List<GuiElement> elements) {
		for (GuiElement element : elements) {
			Point pos = element.getPreferredPos();
			Dimension size = element.getLayoutSize();
			if (size.height < 0 || size.width < 0) {
				Log.error(String.format("Failed to layout element %s!", element));
				continue;
			}
			Rectangle elementBounds = new Rectangle(pos == null ? new Point(0, 0) : pos, size);
			Layout.alignElement(bounds, elementBounds, element.getAlign());
			element.setAssignedBounds(elementBounds);
		}
	}

	@Override
	public Dimension getLayoutSize(ContainerElement container) {
		Dimension preferredSize = container.getPreferredSize();
		int width = preferredSize.width;
		int height = preferredSize.height;
		boolean unboundWidth = width < 0;
		boolean unboundHeight = height < 0;
		width = Math.max(0, width);
		height = Math.max(0, height);
		for (GuiElement element : container.getElements()) {
			Point pos = element.getPreferredPos();
			if (pos == null) {
				pos = new Point(0, 0);
			}
			Dimension size = element.getLayoutSize();
			if (size.width < 0 || size.height < 0) {
				continue;
			}
			Layout.checkSize(element, size);
			if (unboundWidth) {
				int elementWidth = pos.x + size.width;
				width = Math.max(elementWidth, width);
			}
			if (unboundHeight) {
				int elementHeight = pos.y + size.height;
				height = Math.max(elementHeight, height);
			}
		}
		return new Dimension(width, height);
	}
}
