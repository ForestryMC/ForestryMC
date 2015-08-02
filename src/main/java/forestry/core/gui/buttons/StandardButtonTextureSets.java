/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.gui.buttons;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum StandardButtonTextureSets implements IButtonTextureSet {

	LARGE_BUTTON(0, 0, 20, 200),
	SMALL_BUTTON(0, 80, 15, 200),
	LOCKED_BUTTON(224, 0, 16, 16),
	UNLOCKED_BUTTON(240, 0, 16, 16),
	LEFT_BUTTON(204, 0, 16, 10),
	RIGHT_BUTTON(214, 0, 16, 10),
	LEFT_BUTTON_SMALL(238, 220, 12, 9),
	RIGHT_BUTTON_SMALL(247, 220, 12, 9);
	private final int x, y, height, width;

	StandardButtonTextureSets(int x, int y, int height, int width) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

}
