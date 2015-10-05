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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public final class VectUtil {
	public static boolean isAirBlock(World world, IVect position) {
		return world.isAirBlock(position.getPos());
	}

	public static boolean isWoodBlock(World world, IVect position) {
		Block block = getBlock(world, position);
		return block.isWood(world, position.getPos());
	}

	public static TileEntity getTile(World world, IVect position) {
		return world.getTileEntity(position.getPos());
	}

	public static IBlockState getBlockState(World world, IVect position) {
		return world.getBlockState(position.getPos());
	}
	
	public static Block getBlock(World world, IVect position) {
		return getBlockState(world, position).getBlock();
	}

	public static int getBlockMeta(World world, IVect position) {
		return getBlock(world, position).getMetaFromState(getBlockState(world, position));
	}

	public static ItemStack getAsItemStack(World world, IVect position) {
		return new ItemStack(getBlock(world, position), 1, getBlockMeta(world, position));
	}
}
