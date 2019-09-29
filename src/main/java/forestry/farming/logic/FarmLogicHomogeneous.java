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

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmable;
import forestry.api.farming.Soil;
import forestry.core.utils.BlockUtil;

public abstract class FarmLogicHomogeneous extends FarmLogicSoil {
	public FarmLogicHomogeneous(IFarmProperties properties, boolean isManual) {
		super(properties, isManual);
	}

	protected boolean trySetCrop(World world, IFarmHousing farmHousing, BlockPos position, FarmDirection direction) {
		for (IFarmable candidate : getFarmables()) {
			if (farmHousing.plantGermling(candidate, world, position, direction)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean cultivate(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		return maintainSoil(world, farmHousing, pos, direction, extent) || maintainSeedlings(world, farmHousing, pos.up(), direction, extent);
	}

	private boolean maintainSoil(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		if (!farmHousing.canPlantSoil(isManual)) {
			return false;
		}

		for (Soil soil : getSoils()) {
			NonNullList<ItemStack> resources = NonNullList.create();
			resources.add(soil.getResource());
			if (!farmHousing.getFarmInventory().hasResources(resources)) {
				continue;
			}

			for (int i = 0; i < extent; i++) {
				BlockPos position = translateWithOffset(pos, direction, i);
				BlockState soilState = world.getBlockState(position);

				if (!world.isBlockLoaded(position) || farmHousing.isValidPlatform(world, pos)) {
					break;
				}

				if (!BlockUtil.isBreakableBlock(soilState, world, pos) || isAcceptedSoil(soilState)) {
					continue;
				}

				BlockPos platformPosition = position.down();
				if (!farmHousing.isValidPlatform(world, platformPosition)) {
					break;
				}

				BlockUtil.getBlockDrops(world, position).forEach(farmHousing::addPendingProduct);

				BlockUtil.setBlockWithPlaceSound(world, position, soil.getSoilState());
				farmHousing.getFarmInventory().removeResources(resources);
				return true;
			}
		}

		return false;
	}

	protected abstract boolean maintainSeedlings(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent);
}