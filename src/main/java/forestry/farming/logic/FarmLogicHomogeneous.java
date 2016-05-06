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
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.farming.FarmHelper;

public abstract class FarmLogicHomogeneous extends FarmLogic {

	private final ItemStack resource;
	private final ItemStack soilBlock;
	protected final List<IFarmable> germlings;

	List<ItemStack> produce = new ArrayList<>();

	protected FarmLogicHomogeneous(IFarmHousing housing, ItemStack resource, ItemStack soilBlock, Iterable<IFarmable> germlings) {
		super(housing);
		this.resource = resource;
		this.soilBlock = soilBlock;
		this.germlings = new ArrayList<>();
		for (IFarmable germling : germlings) {
			this.germlings.add(germling);
		}
	}

	protected boolean isAcceptedSoil(ItemStack itemStack) {
		return ItemStackUtil.isIdenticalItem(soilBlock, itemStack);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return resource.isItemEqual(itemstack);
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		for (IFarmable germling : germlings) {
			if (germling.isGermling(itemstack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isAcceptedWindfall(ItemStack itemstack) {
		for (IFarmable germling : germlings) {
			if (germling.isWindfall(itemstack)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean cultivate(BlockPos pos, FarmDirection direction, int extent) {

		if (maintainSoil(pos, direction, extent)) {
			return true;
		}

		return maintainGermlings(pos.add(0, 1, 0), direction, extent);
	}

	private boolean maintainSoil(BlockPos pos, FarmDirection direction, int extent) {
		ItemStack[] resources = new ItemStack[]{resource};
		if (!housing.getFarmInventory().hasResources(resources)) {
			return false;
		}

		World world = getWorld();

		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);
			IBlockState blockState1 = world.getBlockState(position);
			Block soil = blockState1.getBlock();

			if (FarmHelper.bricks.contains(soil)) {
				break;
			}

			IBlockState soilState = world.getBlockState(position);
			Block soilBlock = soilState.getBlock();
			ItemStack soilStack = soilBlock.getPickBlock(soilState, null, world, position, null);
			if (isAcceptedSoil(soilStack)) {
				continue;
			}

			BlockPos platformPosition = position.down();
			IBlockState blockState = world.getBlockState(platformPosition);
			Block platformBlock = blockState.getBlock();

			if (!FarmHelper.bricks.contains(platformBlock)) {
				break;
			}

			produce.addAll(BlockUtil.getBlockDrops(world, position));

			setBlock(position, ItemStackUtil.getBlock(this.soilBlock), this.soilBlock.getItemDamage());
			housing.getFarmInventory().removeResources(resources);
			return true;
		}

		return false;
	}

	protected abstract boolean maintainGermlings(BlockPos pos, FarmDirection direction, int extent);
}
