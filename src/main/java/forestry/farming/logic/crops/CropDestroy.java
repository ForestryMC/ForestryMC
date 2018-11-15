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

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.event.ForgeEventFactory;

import forestry.core.config.Constants;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.NetworkUtil;

public class CropDestroy extends Crop {

	protected final IBlockState blockState;
	@Nullable
	protected final IBlockState replantState;

	protected final ItemStack germling;

	public CropDestroy(World world, IBlockState blockState, BlockPos position, @Nullable IBlockState replantState) {
		this(world, blockState, position, replantState, ItemStack.EMPTY);
	}

	public CropDestroy(World world, IBlockState blockState, BlockPos position, @Nullable IBlockState replantState, ItemStack germling) {
		super(world, position);
		this.blockState = blockState;
		this.replantState = replantState;
		this.germling = germling;
	}

	@Override
	protected boolean isCrop(World world, BlockPos pos) {
		return world.getBlockState(pos) == blockState;
	}

	@Override
	protected NonNullList<ItemStack> harvestBlock(World world, BlockPos pos) {
		Block block = blockState.getBlock();
		List<ItemStack> harvested = block.getDrops(world, pos, blockState, 0);    //TODO - remove once harvestcraft (and maybe others) stop using deprecated getDrops
		float chance = ForgeEventFactory.fireBlockHarvesting(harvested, world, pos, blockState, 0, 1.0F, false, null);

		boolean removedSeed = germling.isEmpty();
		Iterator<ItemStack> dropIterator = harvested.iterator();
		while (dropIterator.hasNext()) {
			ItemStack next = dropIterator.next();
			if (world.rand.nextFloat() <= chance) {
				if (!removedSeed && ItemStackUtil.isIdenticalItem(next, germling)) {
					next.shrink(1);
					if (next.isEmpty()) {
						dropIterator.remove();
					}
					removedSeed = true;
				}
			} else {
				dropIterator.remove();
			}
		}

		PacketFXSignal packet = new PacketFXSignal(PacketFXSignal.VisualFXType.BLOCK_BREAK, PacketFXSignal.SoundFXType.BLOCK_BREAK, pos, blockState);
		NetworkUtil.sendNetworkPacket(packet, pos, world);

		if (replantState != null) {
			world.setBlockState(pos, replantState, Constants.FLAG_BLOCK_SYNC);
		} else {
			world.setBlockToAir(pos);
		}
		if (!(harvested instanceof NonNullList)) {
			return NonNullList.from(ItemStack.EMPTY, harvested.toArray(new ItemStack[0]));
		} else {
			return (NonNullList<ItemStack>) harvested;
		}
	}

	@Override
	public String toString() {
		return String.format("CropDestroy [ position: [ %s ]; block: %s ]", position.toString(), blockState);
	}
}
