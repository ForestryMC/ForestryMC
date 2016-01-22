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
package forestry.core.utils.vect;

import forestry.api.farming.FarmDirection;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Represents a position or dimensions.
 */
public abstract class IVect extends BlockPos {
	
	public IVect(int x, int y, int z) {
		super(x, y, z);
	}
	
	public IVect(Entity source) {
		super(source);
	}
	
	public IVect(BlockPos pos) {
		super(pos);
	}
	
	public IVect(double x, double y, double z) {
		super(x, y, z);
	}

	abstract IVect add(IVect other);

	abstract IVect add(EnumFacing direction);

	abstract IVect add(FarmDirection direction);

	abstract IVect add(BlockPos coordinates);

	abstract int[] toArray();
}
