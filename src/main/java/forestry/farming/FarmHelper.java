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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.core.IStructureLogic;
import forestry.api.farming.IFarmComponent;
import forestry.api.farming.IFarmInterface;
import forestry.core.vect.MutableVect;
import forestry.core.vect.Vect;
import forestry.core.vect.VectUtil;
import forestry.farming.gadgets.StructureLogicFarm;

public class FarmHelper implements IFarmInterface {

	@Override
	public IStructureLogic createFarmStructureLogic(IFarmComponent structure) {
		return new StructureLogicFarm(structure);
	}

	public static int getFarmSizeNorthSouth(World world, Vect start) {
		ForgeDirection farmSide = ForgeDirection.NORTH;
		ForgeDirection startSide = ForgeDirection.EAST;

		Vect corner = getFarmMultiblockCorner(world, start, farmSide, startSide);

		return getFarmSizeInDirection(world, corner, farmSide, startSide.getOpposite());
	}

	public static int getFarmSizeEastWest(World world, Vect start) {
		ForgeDirection farmSide = ForgeDirection.EAST;
		ForgeDirection startSide = ForgeDirection.NORTH;

		Vect corner = getFarmMultiblockCorner(world, start, farmSide, startSide);

		return getFarmSizeInDirection(world, corner, farmSide, startSide.getOpposite());
	}

	private static int getFarmSizeInDirection(World world, Vect start, ForgeDirection farmSide, ForgeDirection searchDirection) {
		int size = 0;

		ForgeDirection toCenter = farmSide.getOpposite();

		Vect target = start.add(farmSide);

		TileEntity farmTile;
		do {
			size++;

			target = target.add(searchDirection);

			Vect farmTileLocation = target.add(toCenter);
			farmTile = VectUtil.getTile(world, farmTileLocation);

		} while (farmTile instanceof IFarmComponent);

		return size;
	}

	public static Vect getFarmMultiblockCorner(World world, Vect start, ForgeDirection direction1, ForgeDirection direction2) {
		Vect edge = getFarmMultiblockEdge(world, start, direction1);
		return getFarmMultiblockEdge(world, edge, direction2);
	}

	private static Vect getFarmMultiblockEdge(World world, Vect start, ForgeDirection direction) {
		MutableVect edge = new MutableVect(start);

		while (VectUtil.getTile(world, edge) instanceof IFarmComponent) {
			edge.add(direction);
		}

		edge.add(direction.getOpposite());
		return new Vect(edge);
	}

}
