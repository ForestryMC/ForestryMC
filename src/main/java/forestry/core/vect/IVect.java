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

import forestry.api.farming.FarmDirection;

/**
 * Represents a position or dimensions.
 */
public interface IVect {

	int getX();

	int getY();

	int getZ();

	BlockPos getPos();

	IVect add(IVect other);

	IVect add(int x, int y, int z);

	IVect add(EnumFacing direction);

	IVect add(FarmDirection direction);

	IVect add(BlockPos pos);

	int[] toArray();
}
