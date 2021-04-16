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
package forestry.core.tiles;

import net.minecraft.util.math.BlockPos;

/**
 * Networked tile entities that have a client side "active" flag
 */
public interface IActivatable {

	/**
	 * Position of the tile entity.
	 *
	 * @return The position of the tile entity
	 */
	BlockPos getCoordinates();

	/**
	 * Retrieves the current state of the tile entity.
	 *
	 * @return True if the tile is currently active, false otherwise
	 */
	boolean isActive();

	/**
	 * Changes the state of this tile entity.
	 *
	 * @param active True if the tile should be activated, false otherwise
	 */
	void setActive(boolean active);
}
