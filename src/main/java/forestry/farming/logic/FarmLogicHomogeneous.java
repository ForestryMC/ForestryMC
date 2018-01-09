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
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockUtil;

public abstract class FarmLogicHomogeneous extends FarmLogicSoil {
	protected final List<IFarmable> farmables;

	protected NonNullList<ItemStack> produce = NonNullList.create();

	protected FarmLogicHomogeneous(ItemStack resource, IBlockState soilState, Collection<IFarmable> farmables) {
		addSoil(resource, soilState, false);
		this.farmables = new ArrayList<>(farmables);
	}

	protected boolean isAcceptedSoil(IBlockState blockState) {
		for(Soil soil : soils){
			IBlockState soilState = soil.getSoilState();
			Block soilBlock = soilState.getBlock();
			Block block = blockState.getBlock();
			if(soilState.getBlock() == blockState.getBlock()){
				if(!soil.hasMetaData() || block.getMetaFromState(blockState) == soilBlock.getMetaFromState(soilState)){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		for(Soil soil : soils){
			ItemStack resource = soil.getResource();
			if(resource.isItemEqual(itemstack)){
				return true;
			}
		}
		return false;
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

	protected boolean trySetCrop(World world, IFarmHousing farmHousing, BlockPos position, FarmDirection direction, boolean shuffle) {
		for (IFarmable candidate : farmables) {
			if (farmHousing.plantGermling(candidate, world, position, direction)) {
				return true;
			}
		}

		return false;
	}

	protected boolean trySetCrop(World world, IFarmHousing farmHousing, BlockPos position, FarmDirection direction) {
		for (IFarmable candidate : farmables) {
			if (farmHousing.plantGermling(candidate, world, position, direction)) {
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
		for(Soil soil : soils){
			NonNullList<ItemStack> resources = NonNullList.create();
			resources.add(soil.getResource());
			if (!farmHousing.getFarmInventory().hasResources(resources)) {
				continue;
			}
	
			for (int i = 0; i < extent; i++) {
				BlockPos position = translateWithOffset(pos, direction, i);
				IBlockState soilState = world.getBlockState(position);
	
				if (farmHousing.isValidPlatform(world, pos)) {
					break;
				}
	
				if (isAcceptedSoil(soilState)) {
					continue;
				}
	
				BlockPos platformPosition = position.down();
				if (!farmHousing.isValidPlatform(world, platformPosition)) {
					break;
				}
	
				produce.addAll(BlockUtil.getBlockDrops(world, position));
	
				BlockUtil.setBlockWithPlaceSound(world, position, soil.getSoilState());
				farmHousing.getFarmInventory().removeResources(resources);
				return true;
			}
		}

		return false;
	}

	protected abstract boolean maintainGermlings(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent);
}