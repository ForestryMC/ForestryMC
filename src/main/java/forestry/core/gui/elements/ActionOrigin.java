package forestry.core.gui.elements;

public enum ActionOrigin {
	/**
	 * Never
	 */
	NONE,
	/**
	 * Mouse is hovered over this element
	 */
	SELF,
	/**
	 * Mouse is hovered over this element and it is mouse over widget of the window.
	 */
	SELF_TOP,
	/**
	 * Any element
	 */
	ALL
}
