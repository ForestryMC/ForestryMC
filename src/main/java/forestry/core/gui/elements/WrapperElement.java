package forestry.core.gui.elements;

import java.awt.Dimension;

public abstract class WrapperElement<E extends GuiElement> extends GuiElement {
	protected final E child;

	public WrapperElement(E child) {
		this.child = child;
	}

	@Override
	public Dimension getPreferredSize() {
		return child.getPreferredSize();
	}
}
