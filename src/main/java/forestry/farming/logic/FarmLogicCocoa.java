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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmable;
import forestry.farming.logic.farmables.FarmableCocoa;

public class FarmLogicCocoa extends FarmLogic {
	private final IFarmable cocoa = new FarmableCocoa();

	public FarmLogicCocoa(IFarmProperties properties, boolean isManual) {
		super(properties, isManual);
	}

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(Items.DYE, 1, 3);
	}

	@Override
	public String getUnlocalizedName() {
		return "for.farm.cocoa";
	}

	@Override
	public int getFertilizerConsumption() {
		return 120;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return (int) (20 * hydrationModifier);
	}

	@Override
	public boolean isAcceptedResource(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean isAcceptedGermling(ItemStack itemstack) {
		return cocoa.isGermling(itemstack);
	}

	@Override
	public boolean isAcceptedWindfall(ItemStack stack) {
		return false;
	}

	@Override
	public NonNullList<ItemStack> collect(World world, IFarmHousing farmHousing) {
		return NonNullList.create();
	}

	private final Map<BlockPos, Integer> lastExtentsCultivation = new HashMap<>();

	@Override
	public boolean cultivate(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		if (!lastExtentsCultivation.containsKey(pos)) {
			lastExtentsCultivation.put(pos, 0);
		}

		int lastExtent = lastExtentsCultivation.get(pos);
		if (lastExtent > extent) {
			lastExtent = 0;
		}

		BlockPos position = translateWithOffset(pos.up(), direction, lastExtent);
		boolean result = tryPlantingCocoa(world, farmHousing, position, direction);

		lastExtent++;
		lastExtentsCultivation.put(pos, lastExtent);

		return result;
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

	private boolean tryPlantingCocoa(World world, IFarmHousing farmHousing, BlockPos position, FarmDirection farmDirection) {
		BlockPos.MutableBlockPos current = new BlockPos.MutableBlockPos(position);
		IBlockState blockState = world.getBlockState(current);
		while (isJungleTreeTrunk(blockState)) {
			for (EnumFacing direction : EnumFacing.HORIZONTALS) {
				BlockPos candidate = new BlockPos(current.getX() + direction.getXOffset(), current.getY(), current.getZ() + direction.getZOffset());
				if (world.isBlockLoaded(candidate) && world.isAirBlock(candidate)) {
					return farmHousing.plantGermling(cocoa, world, candidate, farmDirection);
				}
			}

			current.move(EnumFacing.UP);
			if (current.getY() - position.getY() > 1) {
				break;
			}

			blockState = world.getBlockState(current);
		}

		return false;
	}

	private static boolean isJungleTreeTrunk(IBlockState blockState) {
		Block block = blockState.getBlock();
		return block == Blocks.LOG && blockState.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.JUNGLE;
	}

	private Collection<ICrop> getHarvestBlocks(World world, BlockPos position) {

		Set<BlockPos> seen = new HashSet<>();
		Stack<ICrop> crops = new Stack<>();

		// Determine what type we want to harvest.
		IBlockState blockState = world.getBlockState(position);
		Block block = blockState.getBlock();

		ICrop crop = null;
		if (!block.isWood(world, position)) {
			crop = cocoa.getCropAt(world, position, blockState);
			if (crop == null) {
				return crops;
			}
		}

		if (crop != null) {
			crops.add(crop);
		}

		List<BlockPos> candidates = processHarvestBlock(world, crops, seen, position, position);
		List<BlockPos> temp = new ArrayList<>();
		while (!candidates.isEmpty() && crops.size() < 20) {
			for (BlockPos candidate : candidates) {
				temp.addAll(processHarvestBlock(world, crops, seen, position, candidate));
			}
			candidates.clear();
			candidates.addAll(temp);
			temp.clear();
		}
		// Log.finest("Logic %s at %s/%s/%s has seen %s blocks.", getClass().getName(), position.x, position.y, position.z, seen.size());

		return crops;
	}

	private List<BlockPos> processHarvestBlock(World world, Stack<ICrop> crops, Set<BlockPos> seen, BlockPos start, BlockPos position) {
		List<BlockPos> candidates = new ArrayList<>();

		// Get additional candidates to return
		for (int i = -1; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = -1; k < 2; k++) {
					BlockPos candidate = position.add(i, j, k);
					if (candidate.equals(position)) {
						continue;
					}
					if (Math.abs(candidate.getX() - start.getX()) > 5) {
						continue;
					}
					if (Math.abs(candidate.getZ() - start.getZ()) > 5) {
						continue;
					}

					// See whether the given position has already been processed
					if (seen.contains(candidate)) {
						continue;
					}

					if (!world.isBlockLoaded(candidate)) {
						continue;
					}

					IBlockState blockState = world.getBlockState(candidate);
					ICrop crop = cocoa.getCropAt(world, candidate, blockState);
					if (crop != null) {
						crops.push(crop);
						candidates.add(candidate);
						seen.add(candidate);
					} else if (blockState.getBlock().isWood(world, candidate)) {
						candidates.add(candidate);
						seen.add(candidate);
					}
				}
			}
		}

		return candidates;
	}

}
