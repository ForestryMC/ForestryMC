package forestry.core.gui.elements.layouts;

import java.awt.Dimension;

public interface Layout {
	Dimension preferredSize(ElementGroup target);

	Dimension minimumSize(ElementGroup target);

	Dimension maximumSize(ElementGroup target);

	void layoutContainer(ElementGroup parent);

	void invalidateLayout(ElementGroup parent);
}
