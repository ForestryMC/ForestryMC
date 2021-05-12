/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.core.gui.elements;

/**
 * The alignment of the {@link GuiElement} defines the position of the element relative to the position of its parent.
 */
public enum Alignment {
	TOP_LEFT(0.0f, 0.0f),
	TOP_CENTER(0.5f, 0.0f),
	TOP_RIGHT(1.0f, 0.0f),
	MIDDLE_LEFT(0.0f, 0.5f),
	MIDDLE_CENTER(0.5f, 0.5f),
	MIDDLE_RIGHT(1.0f, 0.5f),
	BOTTOM_LEFT(0.0f, 1.0f),
	BOTTOM_CENTER(0.5f, 1.0f),
	BOTTOM_RIGHT(1.0f, 1.0f);

	private final float xOffset;
	private final float yOffset;

	Alignment(float xOffset, float yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public float getXOffset() {
		return xOffset;
	}

	public float getYOffset() {
		return yOffset;
	}
}
