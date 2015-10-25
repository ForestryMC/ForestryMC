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
package forestry.farming;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmComponent;
import forestry.core.vect.MutableVect;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;

public class FarmHelper {

	private static FarmDirection getOpposite(FarmDirection farmDirection) {
		EnumFacing forgeDirection = farmDirection.getDirection();
		EnumFacing forgeDirectionOpposite = forgeDirection.getOpposite();
		return FarmDirection.getFarmDirection(forgeDirectionOpposite);
	}

	public static Vect getFarmMultiblockCorner(World world, Vect start, FarmDirection farmSide,
			FarmDirection layoutDirection) {
		Vect edge = getFarmMultiblockEdge(world, start, farmSide);
		return getFarmMultiblockEdge(world, edge, getOpposite(layoutDirection));
	}

	private static Vect getFarmMultiblockEdge(World world, Vect start, FarmDirection direction) {
		MutableVect edge = new MutableVect(start);

		while (VectUtil.getTile(world, edge) instanceof IFarmComponent) {
			edge.add(direction);
		}

		edge.add(getOpposite(direction));
		return new Vect(edge);
	}

}
