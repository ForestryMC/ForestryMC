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

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.core.utils.BlockUtil;
import forestry.farming.FarmHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public abstract class FarmLogicWatered extends FarmLogic {

	protected final IBlockState ground;
	private final ItemStack resource;

	private static final FluidStack STACK_WATER = new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);

	protected NonNullList<ItemStack> produce = NonNullList.create();

	protected FarmLogicWatered(ItemStack resource, IBlockState ground) {
		this.ground = ground;
		this.resource = resource;
	}

	@Override
	public int getFertilizerConsumption() {
		return 5;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (20 * hydrationModifier);
	}

	protected boolean isAcceptedGround(IBlockState ground) {
		return this.ground.getBlock() == ground.getBlock();
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return resource.isItemEqual(itemstack);
	}

	@Override
	public NonNullList<ItemStack> collect(World world, IFarmHousing farmHousing) {
		NonNullList<ItemStack> products = produce;
		produce = NonNullList.create();
		return products;
	}

	@Override
	public boolean cultivate(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {

		if (maintainSoil(world, farmHousing, pos, direction, extent)) {
			return true;
		}

		if (!isManual && maintainWater(world, farmHousing, pos, direction, extent)) {
			return true;
		}

		if (maintainCrops(world, farmHousing, pos.up(), direction, extent)) {
			return true;
		}

		return false;
	}

	private boolean maintainSoil(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		NonNullList<ItemStack> resources = NonNullList.create();
		resources.add(resource);

		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);
			IBlockState state = world.getBlockState(position);
			if (isAcceptedGround(state) || isWaterSourceBlock(world, position) || !farmHousing.getFarmInventory().hasResources(resources)) {
				continue;
			}

			BlockPos platformPosition = position.down();
			IBlockState blockState = world.getBlockState(platformPosition);
			if (!FarmHelper.bricks.contains(blockState.getBlock())) {
				break;
			}

			if (!BlockUtil.isReplaceableBlock(state, world, position)) {
				produce.addAll(BlockUtil.getBlockDrops(world, position));
				world.setBlockToAir(position);
				return trySetSoil(world, farmHousing, position);
			}

			if (!isManual) {
				if (trySetWater(world, farmHousing, position)) {
					return true;
				}

				return trySetSoil(world, farmHousing, position);
			}
		}

		return false;
	}

	private boolean maintainWater(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		// Still not done, check water then
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);

			BlockPos platformPosition = position.down();
			IBlockState blockState = world.getBlockState(platformPosition);
			if (!FarmHelper.bricks.contains(blockState.getBlock())) {
				break;
			}

			if (trySetWater(world, farmHousing, position)) {
				return true;
			}
		}

		return false;
	}

	protected boolean maintainCrops(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		return false;
	}

	private boolean trySetSoil(World world, IFarmHousing farmHousing, BlockPos position) {
		NonNullList<ItemStack> resources = NonNullList.create();
		resources.add(resource);
		if (!farmHousing.getFarmInventory().hasResources(resources)) {
			return false;
		}
		if (!BlockUtil.setBlockWithPlaceSound(world, position, ground)) {
			return false;
		}
		farmHousing.getFarmInventory().removeResources(resources);
		return true;
	}

	private boolean trySetWater(World world, IFarmHousing farmHousing, BlockPos position) {
		if (isWaterSourceBlock(world, position) || !canPlaceWater(world, position)) {
			return false;
		}

		if (!farmHousing.hasLiquid(STACK_WATER)) {
			return false;
		}

		produce.addAll(BlockUtil.getBlockDrops(world, position));
		BlockUtil.setBlockWithPlaceSound(world, position, Blocks.WATER.getDefaultState());
		farmHousing.removeLiquid(STACK_WATER);
		return true;
	}

	private boolean canPlaceWater(World world, BlockPos position) {
		// don't place water close to other water
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				BlockPos offsetPosition = position.add(x, 0, z);
				if (isWaterSourceBlock(world, offsetPosition)) {
					return false;
				}
			}
		}

		// don't place water if it can flow into blocks next to it
		for (int x = -1; x <= 1; x++) {
			BlockPos offsetPosition = position.add(x, 0, 0);
			if (world.isAirBlock(offsetPosition)) {
				return false;
			}
		}
		for (int z = -1; z <= 1; z++) {
			BlockPos offsetPosition = position.add(0, 0, z);
			if (world.isAirBlock(offsetPosition)) {
				return false;
			}
		}

		return true;
	}

}
