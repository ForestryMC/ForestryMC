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

import forestry.core.PluginCore;
import forestry.core.blocks.BlockBogEarth;
import forestry.core.config.Constants;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.proxy.Proxies;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CropPeat extends Crop {

	public CropPeat(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	protected boolean isCrop(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if (!(block instanceof BlockBogEarth)) {
			return false;
		}

		IBlockState blockState = world.getBlockState(pos);
		BlockBogEarth.SoilType soilType = BlockBogEarth.getTypeFromState(blockState);
		return soilType == BlockBogEarth.SoilType.PEAT;
	}

	@Override
	protected NonNullList<ItemStack> harvestBlock(World world, BlockPos pos) {
		NonNullList<ItemStack> drops = NonNullList.create();
		drops.add(PluginCore.getItems().peat.getItemStack());

		IBlockState blockState = world.getBlockState(pos);

		PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, blockState);
		Proxies.net.sendNetworkPacket(packet, pos, world);

		world.setBlockState(pos, Blocks.DIRT.getDefaultState(), Constants.FLAG_BLOCK_SYNC);
		return drops;
	}

}
