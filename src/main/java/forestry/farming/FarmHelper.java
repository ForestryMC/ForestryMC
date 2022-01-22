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
package forestry.farming;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmListener;
import forestry.api.farming.IFarmLogic;
import forestry.core.utils.VectUtil;

public class FarmHelper {

	public enum Stage {
		CULTIVATE, HARVEST;

		public Stage next() {
			if (this == CULTIVATE) {
				return HARVEST;
			} else {
				return CULTIVATE;
			}
		}
	}

	public static class FarmWorkStatus {
		public boolean didWork = false;
		public boolean hasFarmland = false;
		public boolean hasFertilizer = true;
		public boolean hasLiquid = true;
	}

	public static FarmDirection getLayoutDirection(FarmDirection farmSide) {
		return switch (farmSide) {
			case NORTH -> FarmDirection.WEST;
			case WEST -> FarmDirection.SOUTH;
			case SOUTH -> FarmDirection.EAST;
			case EAST -> FarmDirection.NORTH;
		};
	}

	public static FarmDirection getReversedLayoutDirection(FarmDirection farmSide) {
		return switch (farmSide) {
			case NORTH -> FarmDirection.EAST;
			case WEST -> FarmDirection.NORTH;
			case SOUTH -> FarmDirection.WEST;
			case EAST -> FarmDirection.SOUTH;
		};
	}

	public static final ImmutableSet<Block> bricks = ImmutableSet.of(
		Blocks.BRICKS,
		Blocks.STONE_BRICKS,
		Blocks.SANDSTONE,
		Blocks.NETHER_BRICKS,
		Blocks.QUARTZ_BLOCK
	);

	private static FarmDirection getOpposite(FarmDirection farmDirection) {
		Direction forgeDirection = farmDirection.getFacing();
		Direction forgeDirectionOpposite = forgeDirection.getOpposite();
		return FarmDirection.getFarmDirection(forgeDirectionOpposite);
	}

	/**
	 * @return the corner of the farm for the given side and layout. Returns null if the corner is not in a loaded chunk.
	 */
	private static BlockPos getFarmMultiblockCorner(BlockPos start, FarmDirection farmSide, FarmDirection layoutDirection, BlockPos minFarmCoord, BlockPos maxFarmCoord) {
		BlockPos edge = getFarmMultiblockEdge(start, farmSide, maxFarmCoord, minFarmCoord);
		return getFarmMultiblockEdge(edge, getOpposite(layoutDirection), maxFarmCoord, minFarmCoord);
	}

	/**
	 * @return the edge of the farm for the given starting point and direction.
	 */
	private static BlockPos getFarmMultiblockEdge(BlockPos start, FarmDirection direction, BlockPos maxFarmCoord, BlockPos minFarmCoord) {
		return switch (direction) {
			case NORTH -> // -z
					new BlockPos(start.getX(), start.getY(), minFarmCoord.getZ());
			case EAST -> // +x
					new BlockPos(maxFarmCoord.getX(), start.getY(), start.getZ());
			case SOUTH -> // +z
					new BlockPos(start.getX(), start.getY(), maxFarmCoord.getZ());
			case WEST -> // -x
					new BlockPos(minFarmCoord.getX(), start.getY(), start.getZ());
			default -> throw new IllegalArgumentException("Invalid farm direction: " + direction);
		};
	}

	public static void createTargets(Level world, IFarmHousing farmHousing, Map<FarmDirection, List<FarmTarget>> targets, BlockPos targetStart, final int allowedExtent, final int farmSizeNorthSouth, final int farmSizeEastWest, BlockPos minFarmCoord, BlockPos maxFarmCoord) {
		for (FarmDirection farmSide : FarmDirection.values()) {

			final int farmWidth;
			if (farmSide == FarmDirection.NORTH || farmSide == FarmDirection.SOUTH) {
				farmWidth = farmSizeEastWest;
			} else {
				farmWidth = farmSizeNorthSouth;
			}

			// targets extend sideways in a pinwheel pattern around the farm, so they need to go a little extra distance
			final int targetMaxLimit = allowedExtent + farmWidth;

			FarmDirection layoutDirection = getLayoutDirection(farmSide);

			List<FarmTarget> farmSideTargets = new ArrayList<>();
			targets.put(farmSide, farmSideTargets);

			BlockPos targetLocation = FarmHelper.getFarmMultiblockCorner(targetStart, farmSide, layoutDirection, minFarmCoord, maxFarmCoord);
			BlockPos firstLocation = targetLocation.relative(farmSide.getFacing());
			BlockPos firstGroundPosition = getGroundPosition(world, farmHousing, firstLocation);
			if (firstGroundPosition != null) {
				int groundHeight = firstGroundPosition.getY();

				for (int i = 0; i < allowedExtent; i++) {
					targetLocation = targetLocation.relative(farmSide.getFacing());
					BlockPos groundLocation = new BlockPos(targetLocation.getX(), groundHeight, targetLocation.getZ());

					if (!world.hasChunkAt(groundLocation) || !farmHousing.isValidPlatform(world, groundLocation)) {
						break;
					}

					int targetLimit = targetMaxLimit;
					if (!farmHousing.isSquare()) {
						targetLimit = targetMaxLimit - i - 1;
					}

					FarmTarget target = new FarmTarget(targetLocation, layoutDirection, targetLimit);
					farmSideTargets.add(target);
				}
			}
		}
	}

	@Nullable
	private static BlockPos getGroundPosition(Level world, IFarmHousing farmHousing, BlockPos targetPosition) {
		if (!world.hasChunkAt(targetPosition)) {
			return null;
		}

		for (int yOffset = 2; yOffset > -4; yOffset--) {
			BlockPos position = targetPosition.offset(0, yOffset, 0);
			if (world.hasChunkAt(position) && farmHousing.isValidPlatform(world, position)) {
				return position;
			}
		}

		return null;
	}

	public static boolean isCycleCanceledByListeners(IFarmLogic logic, FarmDirection direction, Iterable<IFarmListener> farmListeners) {
		for (IFarmListener listener : farmListeners) {
			if (listener.cancelTask(logic, direction)) {
				return true;
			}
		}
		return false;
	}

	public static void setExtents(Level world, IFarmHousing farmHousing, Map<FarmDirection, List<FarmTarget>> targets) {
		for (List<FarmTarget> targetsList : targets.values()) {
			if (!targetsList.isEmpty()) {
				BlockPos groundPosition = getGroundPosition(world, farmHousing, targetsList.get(0).getStart());

				for (FarmTarget target : targetsList) {
					target.setExtentAndYOffset(world, groundPosition, farmHousing);
				}
			}
		}
	}

	public static boolean cultivateTarget(Level world, IFarmHousing farmHousing, FarmTarget target, IFarmLogic logic, Iterable<IFarmListener> farmListeners) {
		BlockPos targetPosition = target.getStart().offset(0, target.getYOffset(), 0);
		if (logic.cultivate(world, farmHousing, targetPosition, target.getDirection(), target.getExtent())) {
			for (IFarmListener listener : farmListeners) {
				listener.hasCultivated(logic, targetPosition, target.getDirection(), target.getExtent());
			}
			return true;
		}

		return false;
	}

	public static Collection<ICrop> harvestTargets(Level world, IFarmHousing housing, List<FarmTarget> farmTargets, IFarmLogic logic, Iterable<IFarmListener> farmListeners) {
		for (FarmTarget target : farmTargets) {
			Collection<ICrop> harvested = harvestTarget(world, housing, target, logic, farmListeners);
			if (!harvested.isEmpty()) {
				return harvested;
			}
		}

		return Collections.emptyList();
	}

	public static Collection<ICrop> harvestTarget(Level world, IFarmHousing housing, FarmTarget target, IFarmLogic logic, Iterable<IFarmListener> farmListeners) {
		BlockPos pos = target.getStart().offset(0, target.getYOffset(), 0);
		Collection<ICrop> harvested = logic.harvest(world, housing, target.getDirection(), target.getExtent(), pos);
		if (!harvested.isEmpty()) {
			// Let event handlers know.
			for (IFarmListener listener : farmListeners) {
				listener.hasScheduledHarvest(harvested, logic, pos, target.getDirection(), target.getExtent());
			}
		}
		return harvested;
	}

	public static class TopDownICropComparator implements Comparator<ICrop> {
		public static final TopDownICropComparator INSTANCE = new TopDownICropComparator();

		private TopDownICropComparator() {

		}

		@Override
		public int compare(ICrop o1, ICrop o2) {
			return VectUtil.TOP_DOWN_COMPARATOR.compare(o1.getPosition(), o2.getPosition());
		}
	}
}
