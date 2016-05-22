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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.Farmables;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmable;
import forestry.core.PluginCore;

public class FarmLogicArboreal extends FarmLogicHomogeneous {
	private static final int BRANCH_RANGE = 20;

	public FarmLogicArboreal(ItemStack resource, IBlockState ground, Collection<IFarmable> germlings) {
		super(resource, ground, germlings);
	}

	public FarmLogicArboreal() {
		super(new ItemStack(Blocks.DIRT), PluginCore.blocks.humus.getDefaultState(), Farmables.farmables.get("farmArboreal"));
	}

	@Override
	public String getName() {
		return "Managed Arboretum";
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Item getItem() {
		return Item.getItemFromBlock(Blocks.SAPLING);
	}

	@Override
	public int getFertilizerConsumption() {
		return 10;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (10 * hydrationModifier);
	}

	@Override
	public Collection<ItemStack> collect(World world, IFarmHousing farmHousing) {
		Collection<ItemStack> products = produce;
		produce = collectEntityItems(world, farmHousing, true);
		return products;
	}

	private final Map<BlockPos, Integer> lastExtentsHarvest = new HashMap<>();

	@Override
	public Collection<ICrop> harvest(World world, BlockPos pos, FarmDirection direction, int extent) {

		if (!lastExtentsHarvest.containsKey(pos)) {
			lastExtentsHarvest.put(pos, 0);
		}

		int lastExtent = lastExtentsHarvest.get(pos);
		if (lastExtent > extent) {
			lastExtent = 0;
		}

		BlockPos position = translateWithOffset(pos.up(), direction, lastExtent);
		Collection<ICrop> crops = getHarvestBlocks(world, position);
		lastExtent++;
		lastExtentsHarvest.put(pos, lastExtent);

		return crops;
	}

	private Collection<ICrop> getHarvestBlocks(World world, BlockPos position) {
		Set<BlockPos> seen = new HashSet<>();
		Stack<ICrop> crops = new Stack<>();

		IBlockState blockState = world.getBlockState(position);
		// Determine what type we want to harvest.
		IFarmable germling = null;
		for (IFarmable germl : farmables) {
			ICrop crop = germl.getCropAt(world, position, blockState);
			if (crop != null) {
				crops.push(crop);
				seen.add(position);
				germling = germl;
				break;
			}
		}

		if (germling == null) {
			return crops;
		}

		List<BlockPos> candidates = processHarvestBlock(world, germling, crops, seen, position, position);
		List<BlockPos> temp = new ArrayList<>();
		while (!candidates.isEmpty()) {
			for (BlockPos candidate : candidates) {
				temp.addAll(processHarvestBlock(world, germling, crops, seen, position, candidate));
			}
			candidates.clear();
			candidates.addAll(temp);
			temp.clear();
		}

		return crops;
	}

	private static List<BlockPos> processHarvestBlock(World world, IFarmable germling, Stack<ICrop> crops, Set<BlockPos> seen, BlockPos start, BlockPos position) {
		List<BlockPos> candidates = new ArrayList<>();

		// Get additional candidates to return
		for (int x = -1; x < 2; x++) {
			for (int y = -1; y < 2; y++) {
				for (int z = -1; z < 2; z++) {
					BlockPos candidate = position.add(x, y, z);
					if (candidate.equals(position)) {
						continue;
					}
					if (Math.abs(candidate.getX() - start.getX()) > BRANCH_RANGE) {
						continue;
					}
					if (Math.abs(candidate.getZ() - start.getZ()) > BRANCH_RANGE) {
						continue;
					}

					// See whether the given position has already been processed
					if (seen.contains(candidate)) {
						continue;
					}

					IBlockState blockState = world.getBlockState(position);
					ICrop crop = germling.getCropAt(world, candidate, blockState);
					if (crop != null) {
						crops.push(crop);
						candidates.add(candidate);
						seen.add(candidate);
					}
				}
			}
		}

		return candidates;
	}

	@Override
	protected boolean maintainGermlings(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);

			if (world.isAirBlock(position)) {
				BlockPos soilPosition = position.down();
				IBlockState soilState = world.getBlockState(soilPosition);
				if (isAcceptedSoil(soilState)) {
					return plantSapling(world, farmHousing, position);
				}
			}
		}
		return false;
	}

	private boolean plantSapling(World world, IFarmHousing farmHousing, BlockPos position) {
		Collections.shuffle(farmables);
		for (IFarmable candidate : farmables) {
			if (farmHousing.plantGermling(candidate, world, position)) {
				return true;
			}
		}

		return false;
	}

}
