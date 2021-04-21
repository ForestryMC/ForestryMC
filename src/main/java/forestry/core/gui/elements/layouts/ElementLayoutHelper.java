package forestry.core.gui.elements.layouts;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.lib.GuiElementAlignment;

public class ElementLayoutHelper {
	private final List<ElementLayout> layouts = new ArrayList<>();
	private final LayoutFactory layoutFactory;
	private final int width;
	private final int height;
	private final ElementGroup parent;
	private int xOffset;
	private int yOffset;
	@Nullable
	private ElementLayout currentLayout;
	private boolean horizontal;

	public ElementLayoutHelper(LayoutFactory layoutFactory, int width, int height, ElementGroup parent) {
		this.layoutFactory = layoutFactory;
		this.width = width;
		this.height = height;
		this.parent = parent;
	}

	/**
	 * @return Only false if the helper has no space to add this element.
	 */
	public boolean add(GuiElement element) {
		if (currentLayout == null) {
			layouts.add(currentLayout = layoutFactory.createLayout(0, 0));
			this.horizontal = currentLayout instanceof VerticalLayout;
		}
		int groupWidth = currentLayout.getWidth();
		int groupHeight = currentLayout.getHeight();
		int eleWidth = element.getX() + element.getWidth();
		int eleHeight = element.getY() + element.getHeight();
		if (horizontal) {
			if (yOffset >= height) {
				if (width != 0 && xOffset > width) {
					return false;
				}
				xOffset += currentLayout.getWidth();
				layouts.add(currentLayout = layoutFactory.createLayout(0, 0));
				groupHeight = currentLayout.getHeight();
			}
			groupHeight += eleHeight;
			yOffset = groupHeight;
		} else {
			if (xOffset >= width) {
				if (height != 0 && yOffset > height) {
					return false;
				}
				yOffset += currentLayout.getHeight();
				layouts.add(currentLayout = layoutFactory.createLayout(0, 0));
				groupWidth = currentLayout.getWidth();
			}
			groupWidth += eleWidth;
			xOffset = groupWidth;
		}
		currentLayout.add(element);
		return true;
	}

	public void finish() {
		finish(false);
	}

	public void finish(boolean center) {
		for (GuiElement element : layouts) {
			if (center) {
				element.setAlign(GuiElementAlignment.TOP_CENTER);
			}
			parent.add(element);
		}
		clear();
	}

	public void clear() {
		layouts.clear();
		currentLayout = null;
		xOffset = 0;
		yOffset = 0;
	}

	public Collection<ElementLayout> layouts() {
		return layouts;
	}

	public interface LayoutFactory {
		/**
		 * A factory method to create new layouts if the last layout is full.
		 */
		ElementLayout createLayout(int xOffset, int yOffset);
	}
}
