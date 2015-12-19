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
package forestry.energy.blocks;

import com.google.common.collect.ImmutableList;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import forestry.core.blocks.BlockBase;
import forestry.core.tiles.TileEngine;
import forestry.core.tiles.TileUtil;

public class BlockEngine extends BlockBase<BlockEngineType, BlockEngineType> {
	private static final EnumMap<EnumFacing, List<AxisAlignedBB>> boundingBoxesForDirections = new EnumMap<>(EnumFacing.class);

	static {
		boundingBoxesForDirections.put(EnumFacing.DOWN, ImmutableList.of(
				AxisAlignedBB.fromBounds(0.0, 0.5, 0.0, 1.0, 1.0, 1.0), AxisAlignedBB.fromBounds(0.25, 0.0, 0.25, 0.75, 0.5, 0.75)
		));
		boundingBoxesForDirections.put(EnumFacing.UP, ImmutableList.of(
				AxisAlignedBB.fromBounds(0.0, 0.0, 0.0, 1.0, 0.5, 1.0), AxisAlignedBB.fromBounds(0.25, 0.5, 0.25, 0.75, 1.0, 0.75)
		));
		boundingBoxesForDirections.put(EnumFacing.NORTH, ImmutableList.of(
				AxisAlignedBB.fromBounds(0.0, 0.0, 0.5, 1.0, 1.0, 1.0), AxisAlignedBB.fromBounds(0.25, 0.25, 0.0, 0.75, 0.75, 0.5)
		));
		boundingBoxesForDirections.put(EnumFacing.SOUTH, ImmutableList.of(
				AxisAlignedBB.fromBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.5), AxisAlignedBB.fromBounds(0.25, 0.25, 0.5, 0.75, 0.75, 1.0)
		));
		boundingBoxesForDirections.put(EnumFacing.WEST, ImmutableList.of(
				AxisAlignedBB.fromBounds(0.5, 0.0, 0.0, 1.0, 1.0, 1.0), AxisAlignedBB.fromBounds(0.0, 0.25, 0.25, 0.5, 0.75, 0.75)
		));
		boundingBoxesForDirections.put(EnumFacing.EAST, ImmutableList.of(
				AxisAlignedBB.fromBounds(0.0, 0.0, 0.0, 0.5, 1.0, 1.0), AxisAlignedBB.fromBounds(0.5, 0.25, 0.25, 1.0, 0.75, 0.75)
		));
	}

	public BlockEngine() {
		super(true, setState(BlockEngineType.class));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity colliding) {
		TileEngine tile = TileUtil.getTile(world, x, y, z, TileEngine.class);
		if (tile == null) {
			super.addCollisionBoxesToList(world, x, y, z, mask, list, colliding);
			return;
		}

		ForgeDirection orientation = tile.getOrientation();
		List<AxisAlignedBB> boundingBoxes = boundingBoxesForDirections.get(orientation);
		if (boundingBoxes == null) {
			return;
		}

		for (AxisAlignedBB boundingBoxBase : boundingBoxes) {
			AxisAlignedBB boundingBox = boundingBoxBase.getOffsetBoundingBox(x, y, z);
			if (mask.intersectsWith(boundingBox)) {
				list.add(boundingBox);
			}
		}
	}
	
	@Override
	public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		TileEngine tile = TileUtil.getTile(world, pos, TileEngine.class);
		if (tile == null) {
			super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
			return;
		}

		EnumFacing orientation = tile.getOrientation();
		List<AxisAlignedBB> boundingBoxes = boundingBoxesForDirections.get(orientation);
		if (boundingBoxes == null) {
			return;
		}

		for (AxisAlignedBB boundingBoxBase : boundingBoxes) {
			AxisAlignedBB boundingBox = boundingBoxBase.getOffsetBoundingBox(pos);
			if (mask.intersectsWith(boundingBox)) {
				list.add(boundingBox);
			}
		}
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
		TileEngine tile = TileUtil.getTile(world, pos, TileEngine.class);
		if (tile == null) {
			return super.collisionRayTrace(world, pos, start, end);
		}

		EnumFacing orientation = tile.getOrientation();
		List<AxisAlignedBB> boundingBoxes = boundingBoxesForDirections.get(orientation);
		if (boundingBoxes == null) {
			return super.collisionRayTrace(world, pos, start, end);
		}

		MovingObjectPosition nearestIntersection = null;
		for (AxisAlignedBB boundingBoxBase : boundingBoxes) {
			AxisAlignedBB boundingBox = boundingBoxBase.getOffsetBoundingBox(pos);
			MovingObjectPosition intersection = boundingBox.calculateIntercept(start, end);
			if (intersection != null) {
				if (nearestIntersection == null || (intersection.hitVec.distanceTo(start) < nearestIntersection.hitVec.distanceTo(start))) {
					nearestIntersection = intersection;
				}
			}
		}

		if (nearestIntersection != null) {
			nearestIntersection.blockX = x;
			nearestIntersection.blockY = y;
			nearestIntersection.blockZ = z;
		}

		return nearestIntersection;
	}
}
