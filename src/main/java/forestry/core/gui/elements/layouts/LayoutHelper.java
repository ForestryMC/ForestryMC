package forestry.core.gui.elements.layouts;

import javax.annotation.Nullable;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import forestry.core.gui.elements.Alignment;
import forestry.core.gui.elements.GuiElement;

public class LayoutHelper {
	private final List<ContainerElement> elements = new ArrayList<>();
	private final LayoutFactory factory;
	private final int width;
	private final int height;
	private final ContainerElement parent;
	private int xOffset;
	private int yOffset;
	@Nullable
	private ContainerElement current;
	private boolean horizontal;

	public LayoutHelper(LayoutFactory factory, int width, int height, ContainerElement parent) {
		this.factory = factory;
		this.width = width;
		this.height = height;
		this.parent = parent;
	}

	/**
	 * @return Only false if the helper has no space to add this element.
	 */
	public boolean add(GuiElement element) {
		if (current == null) {
			elements.add(current = factory.createLayout(0, 0));
			this.horizontal = current.layout instanceof FlexLayout && ((FlexLayout) current.layout).isHorizontal();
		}
		//TODO: Maybe layout size ?
		Dimension preferredSize = element.getPreferredSize();
		int widgetWidth = Math.max(preferredSize.width, 0);
		int widgetHeight = Math.max(preferredSize.height, 0);
		if (horizontal) {
			if (xOffset >= width) {
				if (height != 0 && yOffset > height) {
					return false;
				}
				yOffset += current.getHeight();
				elements.add(current = factory.createLayout(0, 0));
				xOffset = 0;
			}
			xOffset += widgetWidth;
		} else {
			if (yOffset >= height) {
				if (width != 0 && xOffset > width) {
					return false;
				}
				xOffset += current.getWidth();
				elements.add(current = factory.createLayout(0, 0));
				yOffset = 0;
			}
			yOffset = widgetHeight;
		}
		current.add(element);
		return true;
	}

	public void finish() {
		finish(false);
	}

	public void finish(boolean center) {
		for (GuiElement element : elements) {
			if (center) {
				element.setAlign(Alignment.TOP_CENTER);
			}
			parent.add(element);
		}
		clear();
	}

	public void clear() {
		elements.clear();
		current = null;
		xOffset = 0;
		yOffset = 0;
	}

	public Collection<ContainerElement> layouts() {
		return elements;
	}

	public interface LayoutFactory {
		/**
		 * A factory method to create new layouts if the last layout is full.
		 */
		ContainerElement createLayout(int xOffset, int yOffset);
	}
}
