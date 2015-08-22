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

import cofh.api.energy.IEnergyReceiver;
import forestry.core.config.Defaults;
import forestry.core.vect.Vect;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockUtil {

	public static ArrayList<ItemStack> getBlockDrops(World world, Vect posBlock) {
		IBlockState state = world.getBlockState(new BlockPos(posBlock.x, posBlock.y, posBlock.z));
		Block block =state.getBlock();

		return (ArrayList<ItemStack>) block.getDrops(world, new BlockPos(posBlock.x, posBlock.y, posBlock.z), state, 0);

	}

	public static boolean isEnergyReceiver(EnumFacing side, TileEntity tile) {
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
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		x += EnumFacing.values()[notchDirection + 2].getFrontOffsetX();
		z += EnumFacing.values()[notchDirection + 2].getFrontOffsetZ();
		Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
		if (block == Blocks.log) {
			return (world.getBlockState(new BlockPos(x, y, z)).getBlock().getMetaFromState(world.getBlockState(new BlockPos(x, y, z))) & 3) == 3;
		} else {
			return block.isWood(world, new BlockPos(x, y, z));
		}
	}

	public static int getMaturityPod(int metadata) {
		return (metadata & 12) >> 2;
	}
}
