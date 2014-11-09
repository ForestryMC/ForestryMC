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

import forestry.core.gui.tooltips.ToolTip;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum LockButtonState implements IMultiButtonState {

	UNLOCKED(new ButtonTextureSet(224, 0, 16, 16)),
	LOCKED(new ButtonTextureSet(240, 0, 16, 16));
	public static final LockButtonState[] VALUES = values();
	private final IButtonTextureSet texture;

	private LockButtonState(IButtonTextureSet texture) {
		this.texture = texture;
	}

	@Override
	public String getLabel() {
		return "";
	}

	@Override
	public IButtonTextureSet getTextureSet() {
		return texture;
	}

	@Override
	public ToolTip getToolTip() {
		return null;
	}

}
