package forestry.core.gui.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import forestry.api.core.IGuiElement;
import forestry.api.core.IGuiElementHelper;
import forestry.api.core.IGuiElementLayout;
import forestry.api.core.IGuiElementLayoutHelper;

public class GuiElementLayoutHelper implements IGuiElementLayoutHelper {
	private final List<IGuiElementLayout> layouts = new ArrayList<>();
	private final LayoutFactory layoutFactory;
	private final int width;
	private final int height;
	private final IGuiElementHelper parent;
	private int xOffset;
	private int yOffset;
	private IGuiElementLayout currentLayout;
	private boolean horizontal;

	public GuiElementLayoutHelper(LayoutFactory layoutFactory, int width, int height, IGuiElementHelper parent) {
		this.layoutFactory = layoutFactory;
		this.width = width;
		this.height = height;
		this.parent = parent;
	}

	/**
	 * @return Only false if the helper has no space to add this element.
	 */
	public boolean add(IGuiElement element) {
		if (currentLayout == null) {
			layouts.add(currentLayout = layoutFactory.createLayout(0, 0));
			this.horizontal = currentLayout instanceof GuiElementVertical;
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
		currentLayout.addElement(element);
		return true;
	}

	@Override
	public void finish() {
		for (IGuiElement element : layouts) {
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

	public Collection<IGuiElementLayout> layouts() {
		return layouts;
	}
}
