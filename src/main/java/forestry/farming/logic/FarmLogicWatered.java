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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.core.fluids.Fluids;
import forestry.core.utils.BlockPosUtil;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.farming.FarmHelper;

public abstract class FarmLogicWatered extends FarmLogic {

	protected final ItemStack ground;
	private final ItemStack resource;

	private static final FluidStack STACK_WATER = Fluids.WATER.getFluid(1000);

	List<ItemStack> produce = new ArrayList<>();

	protected FarmLogicWatered(IFarmHousing housing, ItemStack resource, ItemStack ground) {
		super(housing);
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

	protected boolean isAcceptedGround(ItemStack ground) {
		return ItemStackUtil.isIdenticalItem(this.ground, ground);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return resource.isItemEqual(itemstack);
	}

	@Override
	public Collection<ItemStack> collect() {
		Collection<ItemStack> products = produce;
		produce = new ArrayList<>();
		return products;
	}

	@Override
	public boolean cultivate(BlockPos pos, FarmDirection direction, int extent) {

		if (maintainSoil(pos.getX(), pos.getY(), pos.getZ(), direction, extent)) {
			return true;
		}

		if (!isManual && maintainWater(pos.getX(), pos.getY(), pos.getZ(), direction, extent)) {
			return true;
		}

		if (maintainCrops(pos.getX(), pos.getY() + 1, pos.getZ(), direction, extent)) {
			return true;
		}

		return false;
	}

	private boolean maintainSoil(int x, int y, int z, FarmDirection direction, int extent) {

		World world = getWorld();
		ItemStack[] resources = new ItemStack[]{resource};

		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(x, y, z, direction, i);
			Block soil = BlockPosUtil.getBlock(world, position);

			ItemStack soilStack = BlockPosUtil.getAsItemStack(world, position);
			if (isAcceptedGround(soilStack) || !housing.getFarmInventory().hasResources(resources)) {
				continue;
			}

			BlockPos platformPosition = position.add(0, -1, 0);
			Block platformBlock = BlockPosUtil.getBlock(world, platformPosition);
			if (!FarmHelper.bricks.contains(platformBlock)) {
				break;
			}

			if (!isAirBlock(soil) && !BlockUtil.isReplaceableBlock(soil)) {
				produce.addAll(BlockUtil.getBlockDrops(getWorld(), position));
				setBlock(position, Blocks.air, 0);
				return trySetSoil(position);
			}

			if (isManual || isWaterSourceBlock(world, position)) {
				continue;
			}

			if (trySetWater(world, position)) {
				return true;
			}

			return trySetSoil(position);
		}

		return false;
	}

	private boolean maintainWater(int x, int y, int z, FarmDirection direction, int extent) {
		// Still not done, check water then
		World world = getWorld();
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(x, y, z, direction, i);

			BlockPos platformPosition = position.add(0, -1, 0);
			Block platformBlock = BlockPosUtil.getBlock(world, platformPosition);
			if (!FarmHelper.bricks.contains(platformBlock)) {
				break;
			}

			if (trySetWater(world, position)) {
				return true;
			}
		}

		return false;
	}

	protected boolean maintainCrops(int x, int y, int z, FarmDirection direction, int extent) {
		return false;
	}

	private boolean trySetSoil(BlockPos position) {
		ItemStack[] resources = new ItemStack[]{resource};
		if (!housing.getFarmInventory().hasResources(resources)) {
			return false;
		}
		setBlock(position, ItemStackUtil.getBlock(ground), ground.getItemDamage());
		housing.getFarmInventory().removeResources(resources);
		return true;
	}

	private boolean trySetWater(World world, BlockPos position) {
		if (isWaterSourceBlock(world, position) || !canPlaceWater(world, position)) {
			return false;
		}

		if (!housing.hasLiquid(STACK_WATER)) {
			return false;
		}

		produce.addAll(BlockUtil.getBlockDrops(world, position));
		setBlock(position, Blocks.water, 0);
		housing.removeLiquid(STACK_WATER);
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
			if (BlockPosUtil.isAirBlock(world, offsetPosition)) {
				return false;
			}
		}
		for (int z = -1; z <= 1; z++) {
			BlockPos offsetPosition = position.add(0, 0, z);
			if (BlockPosUtil.isAirBlock(world, offsetPosition)) {
				return false;
			}
		}

		return true;
	}

}
