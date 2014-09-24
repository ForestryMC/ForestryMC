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

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.farming.IFarmHousing;
import forestry.core.config.Defaults;
import forestry.core.utils.LiquidHelper;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;
import forestry.core.utils.Vect;

public abstract class FarmLogicWatered extends FarmLogic {

	protected ItemStack[] ground;
	private final ItemStack[] resource;
	private final ItemStack[] waste;

	private static final FluidStack STACK_WATER = new FluidStack(LiquidHelper.getFluid(Defaults.LIQUID_WATER), 1000);

	ArrayList<ItemStack> produce = new ArrayList<ItemStack>();

	public FarmLogicWatered(IFarmHousing housing, ItemStack[] resource, ItemStack[] ground, ItemStack[] waste) {
		super(housing);
		this.ground = ground;
		this.resource = resource;
		this.waste = waste;
	}

	@Override
	public int getFertilizerConsumption() {
		return 5;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (20 * hydrationModifier);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return resource[0].isItemEqual(itemstack);
	}

	@Override
	public Collection<ItemStack> collect() {
		Collection<ItemStack> products = produce;
		produce = new ArrayList<ItemStack>();
		return products;
	}

	@Override
	public boolean cultivate(int x, int y, int z, ForgeDirection direction, int extent) {

		if (maintainSoil(x, y, z, direction, extent))
			return true;

		if (!isManual && maintainWater(x, y, z, direction, extent))
			return true;

		if (maintainCrops(x, y + 1, z, direction, extent))
			return true;

		return false;
	}

	private boolean isWaste(ItemStack stack) {
		for (ItemStack block : waste)
			if (block.isItemEqual(stack))
				return true;
		return false;
	}

	private boolean maintainSoil(int x, int y, int z, ForgeDirection direction, int extent) {

		World world = getWorld();

		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, y, z, direction, i);
			if (!isAirBlock(position) && !Utils.isReplaceableBlock(world, position.x, position.y, position.z)) {

				ItemStack block = getAsItemStack(position);
				if (isWaste(block) && housing.hasResources(resource)) {
					produce.addAll(StackUtils.getBlock(block).getDrops(world, x, y, z, block.getItemDamage(), 0));
					setBlock(position, Blocks.air, 0);
					return trySetSoil(position);
				}

				continue;
			}

			if (isManual || isWaterBlock(position))
				continue;

			if (i % 2 != 0) {
				ForgeDirection cclock = ForgeDirection.EAST;
				if (direction == ForgeDirection.EAST)
					cclock = ForgeDirection.SOUTH;
				else if (direction == ForgeDirection.SOUTH)
					cclock = ForgeDirection.EAST;
				else if (direction == ForgeDirection.WEST)
					cclock = ForgeDirection.SOUTH;

				Vect previous = translateWithOffset(position.x, position.y, position.z, cclock, 1);
				ItemStack soil = getAsItemStack(previous);
				if (!ground[0].isItemEqual(soil))
					trySetSoil(position);
				continue;
			}

			return trySetSoil(position);
		}

		return false;
	}

	private boolean maintainWater(int x, int y, int z, ForgeDirection direction, int extent) {
		// Still not done, check water then
		World world = getWorld();

		for (int i = 0; i < extent; i++) {
			Vect position = translateWithOffset(x, y, z, direction, i);
			if ((!isAirBlock(position) && !Utils.isReplaceableBlock(world, position.x, position.y, position.z))
					|| isWaterBlock(position)) {
				continue;
			}

			boolean isEnclosed = true;

			if (world.isAirBlock(position.x + 1, position.y, position.z))
				isEnclosed = false;
			else if (world.isAirBlock(position.x - 1, position.y, position.z))
				isEnclosed = false;
			else if (world.isAirBlock(position.x, position.y, position.z + 1))
				isEnclosed = false;
			else if (world.isAirBlock(position.x, position.y, position.z - 1))
				isEnclosed = false;

			if (isEnclosed)
				return trySetWater(position);
		}

		return false;
	}

	protected boolean maintainCrops(int x, int y, int z, ForgeDirection direction, int extent) {
		return false;
	}

	private boolean trySetSoil(Vect position) {
		if (!housing.hasResources(resource))
			return false;
		setBlock(position, StackUtils.getBlock(ground[0]), ground[0].getItemDamage());
		housing.removeResources(resource);
		return true;
	}

	private boolean trySetWater(Vect position) {
		System.out.println("trySetWater: at " + position.toString());

		if (!housing.hasLiquid(STACK_WATER))
			return false;

		System.out.println("Can place water: at " + position.toString());

		setBlock(position, Blocks.water, 0);
		housing.removeLiquid(STACK_WATER);
		return true;
	}

}
