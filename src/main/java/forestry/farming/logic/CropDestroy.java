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
package forestry.farming.logic;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.core.config.Constants;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.proxy.Proxies;

public class CropDestroy extends Crop {

	protected final IBlockState blockState;
	@Nullable
	protected final IBlockState replantState;

	public CropDestroy(World world, IBlockState blockState, BlockPos position, @Nullable IBlockState replantState) {
		super(world, position);
		this.blockState = blockState;
		this.replantState = replantState;
	}

	@Override
	protected boolean isCrop(World world, BlockPos pos) {
		return world.getBlockState(pos) == blockState;
	}

	@Override
	protected NonNullList<ItemStack> harvestBlock(World world, BlockPos pos) {
		Block block = blockState.getBlock();
		NonNullList<ItemStack> harvested = NonNullList.create();
		harvested.addAll(block.getDrops(world, pos, blockState, 0));

		PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, blockState);
		Proxies.net.sendNetworkPacket(packet, pos, world);

		if (replantState != null) {
			world.setBlockState(pos, replantState, Constants.FLAG_BLOCK_SYNC);
		} else {
			world.setBlockToAir(pos);
		}

		return harvested;
	}

	@Override
	public String toString() {
		return String.format("CropDestroy [ position: [ %s ]; block: %s ]", position.toString(), blockState);
	}
}
