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

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.core.config.Constants;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.proxy.Proxies;
import forestry.plugins.compat.PluginIC2;

public class CropRubber extends CropDestroy {

	public CropRubber(World world, IBlockState blockState, BlockPos position) {
		super(world, blockState, position, getReplantState(blockState));
	}

	/**
	 * Convert a "wet" rubber log blockstate into the dry version.
	 * Total hack since we don't have access to the blockstates.
	 */
	private static IBlockState getReplantState(IBlockState sappyState) {
		Block block = sappyState.getBlock();
		int sappyMeta = block.getMetaFromState(sappyState);
		return block.getStateFromMeta(sappyMeta - 4);
	}

	@Override
	protected Collection<ItemStack> harvestBlock(World world, BlockPos pos) {
		Collection<ItemStack> harvested = new ArrayList<>();
		harvested.add(PluginIC2.resin.copy());

		PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, blockState);
		Proxies.net.sendNetworkPacket(packet, world);

		world.setBlockState(pos, replantState, Constants.FLAG_BLOCK_SYNC);
		return harvested;
	}

}
