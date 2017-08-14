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

import java.util.Collection;
import java.util.Stack;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockUtil;

public abstract class FarmLogicCrops extends FarmLogicWatered {
	private final Iterable<IFarmable> seeds;

	protected FarmLogicCrops(Iterable<IFarmable> seeds) {
		super(new ItemStack(Blocks.DIRT), Blocks.FARMLAND.getDefaultState());

		this.seeds = seeds;
	}

	@Override
	public boolean isAcceptedGround(IBlockState blockState) {
		return super.isAcceptedGround(blockState) || blockState.getBlock() == Blocks.FARMLAND;
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		for (IFarmable germling : seeds) {
			if (germling.isGermling(itemstack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isAcceptedWindfall(ItemStack itemstack) {
		for (IFarmable germling : seeds) {
			if (germling.isWindfall(itemstack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public NonNullList<ItemStack> collect(World world, IFarmHousing farmHousing) {
		NonNullList<ItemStack> products = produce;
		produce = collectEntityItems(world, farmHousing, false);
		return products;
	}

	@Override
	protected boolean maintainCrops(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);
			IBlockState state = world.getBlockState(position);
			if (!world.isAirBlock(position) && !BlockUtil.isReplaceableBlock(state, world, position)) {
				continue;
			}

			IBlockState groundState = world.getBlockState(position.down());
			if (isAcceptedGround(groundState)) {
				return trySetCrop(world, farmHousing, position);
			}
		}

		return false;
	}

	private boolean trySetCrop(World world, IFarmHousing farmHousing, BlockPos position) {
		for (IFarmable candidate : seeds) {
			if (farmHousing.plantGermling(candidate, world, position)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Collection<ICrop> harvest(World world, BlockPos pos, FarmDirection direction, int extent) {
		Stack<ICrop> crops = new Stack<>();
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos.up(), direction, i);
			IBlockState blockState = world.getBlockState(position);
			for (IFarmable seed : seeds) {
				ICrop crop = seed.getCropAt(world, position, blockState);
				if (crop != null) {
					crops.push(crop);
					break;
				}
			}
		}
		return crops;
	}

}
