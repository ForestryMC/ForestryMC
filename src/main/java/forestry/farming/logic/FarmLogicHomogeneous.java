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
import java.util.List;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockUtil;
import forestry.farming.FarmHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class FarmLogicHomogeneous extends FarmLogic {

	private final ItemStack resource;
	private final IBlockState soilState;
	protected final List<IFarmable> farmables;

	protected NonNullList<ItemStack> produce = NonNullList.create();

	protected FarmLogicHomogeneous(ItemStack resource, IBlockState soilState, Collection<IFarmable> farmables) {
		this.resource = resource;
		this.soilState = soilState;
		this.farmables = new ArrayList<>(farmables);
	}

	protected boolean isAcceptedSoil(IBlockState blockState) {
		return soilState.getBlock() == blockState.getBlock();
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return resource.isItemEqual(itemstack);
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		for (IFarmable germling : farmables) {
			if (germling.isGermling(itemstack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isAcceptedWindfall(ItemStack itemstack) {
		for (IFarmable germling : farmables) {
			if (germling.isWindfall(itemstack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean cultivate(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		return maintainSoil(world, farmHousing, pos, direction, extent) ||
				maintainGermlings(world, farmHousing, pos.up(), direction, extent);
	}

	private boolean maintainSoil(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		NonNullList<ItemStack> resources = NonNullList.create();
		resources.add(resource);
		if (!farmHousing.getFarmInventory().hasResources(resources)) {
			return false;
		}

		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);
			IBlockState soilState = world.getBlockState(position);

			if (FarmHelper.bricks.contains(soilState.getBlock())) {
				break;
			}

			if (isAcceptedSoil(soilState)) {
				continue;
			}

			BlockPos platformPosition = position.down();
			IBlockState platformState = world.getBlockState(platformPosition);
			if (!FarmHelper.bricks.contains(platformState.getBlock())) {
				break;
			}

			produce.addAll(BlockUtil.getBlockDrops(world, position));

			BlockUtil.setBlockWithPlaceSound(world, position, this.soilState);
			farmHousing.getFarmInventory().removeResources(resources);
			return true;
		}

		return false;
	}

	protected abstract boolean maintainGermlings(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent);
}
