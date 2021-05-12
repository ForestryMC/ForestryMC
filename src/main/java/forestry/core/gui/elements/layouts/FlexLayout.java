package forestry.core.gui.elements.layouts;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import forestry.core.gui.elements.GuiElement;
import forestry.core.utils.Log;

public class FlexLayout implements Layout {

	public static final Insets DEFAULT_MARGIN = new Insets(0, 0, 0, 0);
	public static final Insets LEFT_MARGIN = new Insets(0, 4, 0, 0);

	public static FlexLayout horizontal(int spacing) {
		return horizontal(spacing, DEFAULT_MARGIN);
	}

	public static FlexLayout vertical(int spacing) {
		return vertical(spacing, DEFAULT_MARGIN);
	}

	public static FlexLayout horizontal(int spacing, Insets margin) {
		return new FlexLayout(Direction.HORIZONTAL, spacing, margin);
	}

	public static FlexLayout vertical(int spacing, Insets margin) {
		return new FlexLayout(Direction.VERTICAL, spacing, margin);
	}

	protected final Direction direction;
	protected final int spacing;
	protected final Insets margin;

	protected FlexLayout(Direction direction, int spacing) {
		this(direction, spacing, DEFAULT_MARGIN);
	}

	protected FlexLayout(Direction direction, int spacing, Insets margin) {
		this.direction = direction;
		this.spacing = spacing;
		this.margin = margin;
	}

	public boolean isHorizontal() {
		return direction == Direction.HORIZONTAL;
	}

	@Override
	public void layoutContainer(Rectangle bounds, List<GuiElement> elements) {
		int sizeExtent = getDirectionExtent(bounds.getSize());
		if (sizeExtent > 0) {
			handleBound(bounds, elements);
		} else {
			handleUnbound(bounds, elements);
		}
	}

	@Override
	public Dimension getLayoutSize(ContainerElement container) {
		Dimension preferredSize = container.getPreferredSize();
		int sizeExtent = getDirectionExtent(preferredSize);
		int oppositeExtent = getOppositeExtent(preferredSize);
		boolean unboundExtent = sizeExtent < 0;
		boolean unboundOpposite = oppositeExtent < 0;
		if (unboundExtent) {
			sizeExtent = (direction == Direction.HORIZONTAL ? margin.left : margin.top);
		}
		if (unboundOpposite) {
			sizeExtent = (direction == Direction.HORIZONTAL ? margin.top : margin.left);
		}
		//unbound, extent will be calculated from the child elements
		for (GuiElement element : container.getElements()) {
			Dimension elementSize = new Dimension(element.getLayoutSize());
			Layout.checkSize(element, elementSize);
			int elementExtent = getDirectionExtent(elementSize);
			int elementOpposite = getOppositeExtent(elementSize);
			if (elementExtent < 0) {
				Log.error(String.format("Tried to an widget with unbound extent to a flex layout. Direction: %s; Widget: %s; Size:%s", direction, element, elementSize));
				continue;
			}
			if (unboundExtent) {
				sizeExtent += elementExtent;
			}
			oppositeExtent = Math.max(oppositeExtent, elementOpposite);
		}
		return direction == Direction.HORIZONTAL ?
				new Dimension(sizeExtent, oppositeExtent) :
				new Dimension(oppositeExtent, sizeExtent);
	}

	protected void handleUnbound(Rectangle bounds, List<GuiElement> elements) {
		int sizeExtent = (direction == Direction.HORIZONTAL ? margin.left : margin.top);
		for (GuiElement element : elements) {
			Dimension preferredSize = new Dimension(element.getLayoutSize());
			int elementExtent = getDirectionExtent(preferredSize);
			if (elementExtent < 0) {
				Log.error(String.format("Tried to an widget with unbound extent to a flex layout. Direction: %s; Widget: %s; Size:%s", direction, element, preferredSize));
				continue;
			}
			sizeExtent += elementExtent;
			Point pos = new Point();
			if (direction == Direction.HORIZONTAL) {
				pos.x = sizeExtent;
				if (preferredSize.height < 0) {
					preferredSize.height = bounds.height;
				}
			} else {
				pos.y = sizeExtent;
				if (preferredSize.width < 0) {
					preferredSize.width = bounds.width;
				}
			}
			Rectangle elementBounds = new Rectangle(pos, preferredSize);
			Layout.alignElement(bounds, elementBounds, element.getAlign(), direction);
			element.setAssignedBounds(elementBounds);
		}
		sizeExtent += (direction == Direction.HORIZONTAL ? margin.right : margin.bottom);
		if (direction == Direction.HORIZONTAL) {
			bounds.width = sizeExtent;
		} else {
			bounds.height = sizeExtent;
		}
	}

	protected void handleBound(Rectangle bounds, List<GuiElement> elements) {
		int sizeExtent = getDirectionExtent(bounds.getSize());
		sizeExtent -= (direction == Direction.HORIZONTAL ? margin.left : margin.top)
				+ (direction == Direction.HORIZONTAL ? margin.right : margin.bottom);
		int flexExtent = getFlexExtent(elements, sizeExtent);
		int position = direction == Direction.HORIZONTAL ? margin.left : margin.top;
		for (GuiElement element : elements) {
			Dimension size = new Dimension(element.getLayoutSize());
			Point pos = new Point();
			if (direction == Direction.HORIZONTAL) {
				pos.x = position;
				if (size.width < 0) {
					size.width = flexExtent;
				}
				position += spacing + size.width;
			} else {
				pos.y = position;
				if (size.height < 0) {
					size.height = flexExtent;
				}
				position += spacing + size.height;
			}
			Rectangle elementBounds = new Rectangle(pos, size);
			Layout.alignElement(bounds, elementBounds, element.getAlign(), direction);
			element.setAssignedBounds(elementBounds);
		}
	}

	/**
	 * Calculates the extent that all elements with an unknown value in the given direction have.
	 */
	private int getFlexExtent(List<GuiElement> elements, int totalSize) {
		//The extent of all elements with an known extent added together
		int usedExtent = 0;
		//The amount of element with an known extent
		int usedCount = 0;
		for (GuiElement element : elements) {
			Dimension preferredSize = element.getLayoutSize();
			int elementExtent = getDirectionExtent(preferredSize);
			if (elementExtent > 0) {
				usedExtent += elementExtent;
				usedCount++;
			}
		}
		//Adding spacing for every element, event these with unknown size. There is always one spacing less than elements
		usedExtent += (elements.size() - 1) * spacing;
		//Amount of unknown elements (difference from the amount of existing elements and the amount of known elements)
		int unknownCount = elements.size() - usedCount;
		if (unknownCount == 0) {
			return 0;
		}
		return (totalSize - usedExtent) / unknownCount;
	}

	private int getDirectionExtent(Dimension preferredSize) {
		return direction == Direction.HORIZONTAL ? preferredSize.width : preferredSize.height;
	}

	private int getOppositeExtent(Dimension preferredSize) {
		return direction == Direction.HORIZONTAL ? preferredSize.height : preferredSize.width;
	}
}
