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
import net.minecraft.util.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.config.Defaults;
import forestry.core.vect.Vect;

import cofh.api.energy.IEnergyReceiver;

public class BlockUtil {

	public static ArrayList<ItemStack> getBlockDrops(World world, Vect posBlock) {
		Block block = world.getBlock(posBlock.x, posBlock.y, posBlock.z);
		int meta = world.getBlockMetadata(posBlock.x, posBlock.y, posBlock.z);

		return block.getDrops(world, posBlock.x, posBlock.y, posBlock.z, meta, 0);

	}

	public static boolean isEnergyReceiver(ForgeDirection side, TileEntity tile) {
		if (!(tile instanceof IEnergyReceiver)) {
			return false;
		}

		IEnergyReceiver receptor = (IEnergyReceiver) tile;
		return receptor.canConnectEnergy(side);
	}

	public static boolean tryPlantPot(World world, BlockPos pos, Block block) {

		int direction = getDirectionalMetadata(world, pos);
		if (direction < 0) {
			return false;
		}

		world.setBlockState(pos, block.getStateFromMeta(direction), Defaults.FLAG_BLOCK_SYNCH);
		return true;
	}

	public static int getDirectionalMetadata(World world, BlockPos pos) {
		for (int i = 0; i < 4; i++) {
			if (!isValidPot(world, pos, i)) {
				continue;
			}
			return i;
		}
		return -1;
	}

	public static boolean isValidPot(World world, BlockPos pos, int notchDirection) {
		x += Direction.offsetX[notchDirection];
		z += Direction.offsetZ[notchDirection];
		Block block = world.getBlockState(pos).getBlock();
		if (block == Blocks.log) {
			return BlockLog.func_150165_c(world.getBlockMetadata(x, y, z)) == 3;
		} else {
			return block.isWood(world, pos);
		}
	}

	public static int getMaturityPod(int metadata) {
		return BlockCocoa.func_149987_c(metadata);
	}
}
