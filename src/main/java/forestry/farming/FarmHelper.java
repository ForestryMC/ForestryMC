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

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.farming.FarmDirection;
import forestry.api.multiblock.IFarmComponent;
import forestry.core.utils.vect.MutableVect;
import forestry.core.utils.vect.Vect;
import forestry.core.utils.vect.VectUtil;

public class FarmHelper {

	public static final ImmutableSet<Block> bricks = ImmutableSet.of(
			Blocks.brick_block,
			Blocks.stonebrick,
			Blocks.sandstone,
			Blocks.nether_brick,
			Blocks.quartz_block
	);

	private static FarmDirection getOpposite(FarmDirection farmDirection) {
		ForgeDirection forgeDirection = farmDirection.getForgeDirection();
		ForgeDirection forgeDirectionOpposite = forgeDirection.getOpposite();
		return FarmDirection.getFarmDirection(forgeDirectionOpposite);
	}

	public static Vect getFarmMultiblockCorner(World world, Vect start, FarmDirection farmSide, FarmDirection layoutDirection) {
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
