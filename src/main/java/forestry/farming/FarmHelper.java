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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmListener;
import forestry.api.farming.IFarmLogic;
import forestry.core.utils.TopDownBlockPosComparator;

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

	private static FarmDirection getLayoutDirection(FarmDirection farmSide) {
		switch (farmSide) {
			case NORTH:
				return FarmDirection.WEST;
			case WEST:
				return FarmDirection.SOUTH;
			case SOUTH:
				return FarmDirection.EAST;
			case EAST:
				return FarmDirection.NORTH;
		}
		return null;
	}

	public static final ImmutableSet<Block> bricks = ImmutableSet.of(
		Blocks.BRICK_BLOCK,
		Blocks.STONEBRICK,
		Blocks.SANDSTONE,
		Blocks.NETHER_BRICK,
		Blocks.QUARTZ_BLOCK
	);

	private static FarmDirection getOpposite(FarmDirection farmDirection) {
		EnumFacing forgeDirection = farmDirection.getFacing();
		EnumFacing forgeDirectionOpposite = forgeDirection.getOpposite();
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
		switch (direction) {
			case NORTH: // -z
				return new BlockPos(start.getX(), start.getY(), minFarmCoord.getZ());
			case EAST: // +x
				return new BlockPos(maxFarmCoord.getX(), start.getY(), start.getZ());
			case SOUTH: // +z
				return new BlockPos(start.getX(), start.getY(), maxFarmCoord.getZ());
			case WEST: // -x
				return new BlockPos(minFarmCoord.getX(), start.getY(), start.getZ());
			default:
				throw new IllegalArgumentException("Invalid farm direction: " + direction);
		}
	}

	public static void createTargets(World world, IFarmHousing farmHousing, Map<FarmDirection, List<FarmTarget>> targets, BlockPos targetStart, final int allowedExtent, final int farmSizeNorthSouth, final int farmSizeEastWest, BlockPos minFarmCoord, BlockPos maxFarmCoord) {
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
			BlockPos firstLocation = targetLocation.offset(farmSide.getFacing());
			BlockPos firstGroundPosition = getGroundPosition(world, farmHousing, firstLocation);
			if (firstGroundPosition != null) {
				int groundHeight = firstGroundPosition.getY();

				for (int i = 0; i < allowedExtent; i++) {
					targetLocation = targetLocation.offset(farmSide.getFacing());
					BlockPos groundLocation = new BlockPos(targetLocation.getX(), groundHeight, targetLocation.getZ());

					if (!world.isBlockLoaded(groundLocation) || !farmHousing.isValidPlatform(world, groundLocation)) {
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
	private static BlockPos getGroundPosition(World world, IFarmHousing farmHousing, BlockPos targetPosition) {
		if (!world.isBlockLoaded(targetPosition)) {
			return null;
		}

		for (int yOffset = 2; yOffset > -4; yOffset--) {
			BlockPos position = targetPosition.add(0, yOffset, 0);
			if (world.isBlockLoaded(position) && farmHousing.isValidPlatform(world, position)) {
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

	public static void setExtents(World world, IFarmHousing farmHousing, Map<FarmDirection, List<FarmTarget>> targets) {
		for (List<FarmTarget> targetsList : targets.values()) {
			if (!targetsList.isEmpty()) {
				BlockPos groundPosition = getGroundPosition(world, farmHousing, targetsList.get(0).getStart());

				for (FarmTarget target : targetsList) {
					target.setExtentAndYOffset(world, groundPosition, farmHousing);
				}
			}
		}
	}

	public static boolean cultivateTarget(World world, IFarmHousing farmHousing, FarmTarget target, IFarmLogic logic, Iterable<IFarmListener> farmListeners) {
		BlockPos targetPosition = target.getStart().add(0, target.getYOffset(), 0);
		if (logic.cultivate(world, farmHousing, targetPosition, target.getDirection(), target.getExtent())) {
			for (IFarmListener listener : farmListeners) {
				listener.hasCultivated(logic, targetPosition, target.getDirection(), target.getExtent());
			}
			return true;
		}

		return false;
	}

	public static Collection<ICrop> harvestTargets(World world, List<FarmTarget> farmTargets, IFarmLogic logic, Iterable<IFarmListener> farmListeners) {
		for (FarmTarget target : farmTargets) {
			Collection<ICrop> harvested = harvestTarget(world, target, logic, farmListeners);
			if (!harvested.isEmpty()) {
				return harvested;
			}
		}

		return Collections.emptyList();
	}

	public static Collection<ICrop> harvestTarget(World world, FarmTarget target, IFarmLogic logic, Iterable<IFarmListener> farmListeners) {
		BlockPos pos = target.getStart().add(0, target.getYOffset(), 0);
		Collection<ICrop> harvested = logic.harvest(world, pos, target.getDirection(), target.getExtent());
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
			return TopDownBlockPosComparator.INSTANCE.compare(o1.getPosition(), o2.getPosition());
		}
	}
}
