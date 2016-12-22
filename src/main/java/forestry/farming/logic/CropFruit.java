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

import forestry.api.genetics.IFruitBearer;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.proxy.Proxies;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CropFruit extends Crop {

	public CropFruit(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	protected boolean isCrop(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof IFruitBearer)) {
			return false;
		}
		IFruitBearer bearer = (IFruitBearer) tile;
		if (!bearer.hasFruit()) {
			return false;
		}
		if (bearer.getRipeness() < 0.9f) {
			return false;
		}

		return true;
	}

	@Override
	protected NonNullList<ItemStack> harvestBlock(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof IFruitBearer)) {
			return NonNullList.create();
		}

		IBlockState blockState = world.getBlockState(pos);
		PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, blockState);
		Proxies.net.sendNetworkPacket(packet, pos, world);
		return ((IFruitBearer) tile).pickFruit(ItemStack.EMPTY);
	}

}
