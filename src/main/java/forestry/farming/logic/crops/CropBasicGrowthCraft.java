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
package forestry.farming.logic.crops;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.core.config.Constants;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.NetworkUtil;

public class CropBasicGrowthCraft extends Crop {

	private final IBlockState blockState;
	private final boolean isRice;
	private final boolean isGrape;

	public CropBasicGrowthCraft(World world, IBlockState blockState, BlockPos position, boolean isRice, boolean isGrape) {
		super(world, position);
		this.blockState = blockState;
		this.isRice = isRice;
		this.isGrape = isGrape;
	}

	@Override
	protected boolean isCrop(World world, BlockPos pos) {
		return world.getBlockState(pos) == blockState;
	}

	@Override
	protected NonNullList<ItemStack> harvestBlock(World world, BlockPos pos) {
		Block block = blockState.getBlock();
		NonNullList<ItemStack> harvest = NonNullList.create();
		block.getDrops(harvest, world, pos, blockState, 0);
		if (harvest.size() > 1) {
			harvest.remove(0); //Hops have rope as first drop.
		}

		PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, blockState);
		NetworkUtil.sendNetworkPacket(packet, pos, world);

		if (isGrape) {
			world.setBlockToAir(pos);
		} else {
			world.setBlockState(pos, block.getDefaultState(), Constants.FLAG_BLOCK_SYNC);
		}

		if (isRice) {
			// TODO: GrowthCraft for MC 1.9. Don't use meta, get the actual block state.
			world.setBlockState(pos.down(), block.getStateFromMeta(7), Constants.FLAG_BLOCK_SYNC);
		}

		return harvest;
	}

	@Override
	public String toString() {
		return String.format("CropBasicGrowthCraft [ position: [ %s ]; block: %s ]", position.toString(), blockState);
	}
}
