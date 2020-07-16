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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmable;
import forestry.api.farming.Soil;
import forestry.core.utils.BlockUtil;
import forestry.farming.logic.farmables.FarmableCocoa;


public class FarmLogicCocoa extends FarmLogicSoil {
	private static final int[] LAYOUT_POSITIONS = new int[]{4, 1, 3, 0, 2};
	private final IFarmable cocoa = new FarmableCocoa();

	public FarmLogicCocoa(IFarmProperties properties, boolean isManual) {
		super(properties, isManual);
	}

	@Override
	public boolean cultivate(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		if (maintainSoil(world, farmHousing, pos, direction, extent)) {
			return true;
		}
		BlockPos position = farmHousing.getValidPosition(direction, pos, extent, pos.up());
		boolean result = tryPlantingCocoa(world, farmHousing, position, direction);

		farmHousing.increaseExtent(direction, pos, extent);

		return result;
	}

	protected boolean maintainSoil(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		if (!farmHousing.canPlantSoil(isManual)) {
			return false;
		}
		BlockPos cornerPos = farmHousing.getFarmCorner(direction);
		int distance = getDistanceValue(direction.getFacing().rotateY(), cornerPos, pos) - 1;
		int layoutExtent = LAYOUT_POSITIONS[distance % LAYOUT_POSITIONS.length];
		for (Soil soil : getSoils()) {
			NonNullList<ItemStack> resources = NonNullList.create();
			resources.add(soil.getResource());

			for (int i = 0; i < extent; i++) {
				BlockPos position = translateWithOffset(pos, direction, i);
				if (!world.isBlockLoaded(position)) {
					break;
				}

				if (!isValidPosition(direction, position, pos, layoutExtent)
					|| !farmHousing.getFarmInventory().hasResources(resources)) {
					continue;
				}

				BlockPos platformPosition = position.down();
				if (!farmHousing.isValidPlatform(world, platformPosition)) {
					break;
				}

				for (int z = 0; z < 3; z++) {
					BlockPos location = position.up(z);

					BlockState state = world.getBlockState(location);
					if (z == 0 && !world.isAirBlock(location)
						|| z > 0 && isAcceptedSoil(state)
						|| !BlockUtil.isBreakableBlock(state, world, pos)) {
						continue;
					}

					if (!BlockUtil.isReplaceableBlock(state, world, location)) {
						BlockUtil.getBlockDrops(world, location).forEach(farmHousing::addPendingProduct);
						world.setBlockState(location, Blocks.AIR.getDefaultState());
						return trySetSoil(world, farmHousing, location, soil.getResource(), soil.getSoilState());
					}

					if (!isManual) {
						return trySetSoil(world, farmHousing, location, soil.getResource(), soil.getSoilState());
					}
				}
			}
		}

		return false;
	}

	protected int getDistanceValue(Direction facing, BlockPos posA, BlockPos posB) {
		BlockPos delta = posA.subtract(posB);
		int value;
		switch (facing.getAxis()) {
			case X:
				value = delta.getX();
				break;
			case Y:
				value = delta.getY();
				break;
			case Z:
				value = delta.getZ();
				break;
			default:
				value = 0;
		}
		return Math.abs(value);
	}

	//4, 1, 3, 0, 2
	protected boolean isValidPosition(FarmDirection direction, BlockPos pos, BlockPos logicPos, int layoutExtent) {
		int distance = getDistanceValue(direction.getFacing(), pos, logicPos);
		return (distance % LAYOUT_POSITIONS.length) == (layoutExtent);
	}

	protected boolean trySetSoil(World world, IFarmHousing farmHousing, BlockPos position, ItemStack resource, BlockState ground) {
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

	@Override
	public Collection<ICrop> harvest(World world, IFarmHousing housing, FarmDirection direction, int extent, BlockPos pos) {
		BlockPos position = housing.getValidPosition(direction, pos, extent, pos.up());
		Collection<ICrop> crops = getHarvestBlocks(world, position);
		housing.increaseExtent(direction, pos, extent);

		return crops;
	}

	private boolean tryPlantingCocoa(World world, IFarmHousing farmHousing, BlockPos position, FarmDirection farmDirection) {
		BlockPos.Mutable current = new BlockPos.Mutable();
		BlockState blockState = world.getBlockState(current.setPos(position));
		while (isJungleTreeTrunk(blockState)) {
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				BlockPos candidate = new BlockPos(current.getX() + direction.getXOffset(), current.getY(), current.getZ() + direction.getZOffset());
				if (world.isBlockLoaded(candidate) && world.isAirBlock(candidate)) {
					return farmHousing.plantGermling(cocoa, world, candidate, farmDirection);
				}
			}

			current.move(Direction.UP);
			if (current.getY() - position.getY() > 1) {
				break;
			}

			blockState = world.getBlockState(current);
		}

		return false;
	}

	private static boolean isJungleTreeTrunk(BlockState blockState) {
		Block block = blockState.getBlock();
		//TODO - hopefully this is OK
		return block == Blocks.JUNGLE_LOG;
	}

	private Collection<ICrop> getHarvestBlocks(World world, BlockPos position) {

		Set<BlockPos> seen = new HashSet<>();
		Stack<ICrop> crops = new Stack<>();

		// Determine what type we want to harvest.
		BlockState blockState = world.getBlockState(position);
		Block block = blockState.getBlock();

		ICrop crop = null;
		if (!block.isIn(BlockTags.LOGS)) {
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

					BlockState blockState = world.getBlockState(candidate);
					ICrop crop = cocoa.getCropAt(world, candidate, blockState);
					if (crop != null) {
						crops.push(crop);
						candidates.add(candidate);
						seen.add(candidate);
					} else if (blockState.getBlock().isIn(BlockTags.LOGS)) {
						candidates.add(candidate);
						seen.add(candidate);
					}
				}
			}
		}

		return candidates;
	}

}
