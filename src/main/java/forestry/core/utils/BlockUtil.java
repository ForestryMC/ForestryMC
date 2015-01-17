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
package forestry.core.utils;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockLog;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.config.Defaults;
import forestry.core.vect.Vect;

import cofh.api.energy.IEnergyHandler;

public class BlockUtil {

	public static ArrayList<ItemStack> getBlockDrops(World world, Vect posBlock) {
		Block block = world.getBlock(posBlock.x, posBlock.y, posBlock.z);
		int meta = world.getBlockMetadata(posBlock.x, posBlock.y, posBlock.z);

		return block.getDrops(world, posBlock.x, posBlock.y, posBlock.z, meta, 0);

	}

	public static boolean isRFTile(ForgeDirection side, TileEntity tile) {
		if (tile == null)
			return false;

		if (!(tile instanceof IEnergyHandler))
			return false;

		IEnergyHandler receptor = (IEnergyHandler) tile;
		return receptor.canConnectEnergy(side);
	}

	public static boolean tryPlantPot(World world, int x, int y, int z, Block block) {

		int direction = getDirectionalMetadata(world, x, y, z);
		if (direction < 0)
			return false;

		world.setBlock(x, y, z, block, direction, Defaults.FLAG_BLOCK_SYNCH);
		return true;
	}

	public static int getDirectionalMetadata(World world, int x, int y, int z) {
		for (int i = 0; i < 4; i++) {
			if (!isValidPot(world, x, y, z, i))
				continue;
			return i;
		}
		return -1;
	}

	public static boolean isValidPot(World world, int x, int y, int z, int notchDirection) {
		x += Direction.offsetX[notchDirection];
		z += Direction.offsetZ[notchDirection];
		Block block = world.getBlock(x, y, z);
		if (block == Blocks.log)
			return BlockLog.func_150165_c(world.getBlockMetadata(x, y, z)) == 3;
		else
			return block.isWood(world, x, y, z);
	}

	public static int getMaturityPod(int metadata) {
		return BlockCocoa.func_149987_c(metadata);
	}
}
