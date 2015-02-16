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
package forestry.core.vect;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Represents a position or dimensions.
 */
//TODO Either remove or give a big refactor.
public interface IVect {
	int getX();

	int getY();

	int getZ();

	IVect add(IVect other);

	IVect add(int x, int y, int z);

	IVect add(EnumFacing direction);

	BlockPos toBlockPos();
}
