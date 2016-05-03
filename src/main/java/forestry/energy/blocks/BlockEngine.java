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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import forestry.core.blocks.BlockBase;
import forestry.core.tiles.TileEngine;
import forestry.core.tiles.TileUtil;

public class BlockEngine extends BlockBase<BlockTypeEngine> {
	private static final EnumMap<EnumFacing, List<AxisAlignedBB>> boundingBoxesForDirections = new EnumMap<>(EnumFacing.class);

	static {
		boundingBoxesForDirections.put(EnumFacing.DOWN, ImmutableList.of(
				new AxisAlignedBB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0), new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.5, 0.75)
		));
		boundingBoxesForDirections.put(EnumFacing.UP, ImmutableList.of(
				new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0), new AxisAlignedBB(0.25, 0.5, 0.25, 0.75, 1.0, 0.75)
		));
		boundingBoxesForDirections.put(EnumFacing.NORTH, ImmutableList.of(
				new AxisAlignedBB(0.0, 0.0, 0.5, 1.0, 1.0, 1.0), new AxisAlignedBB(0.25, 0.25, 0.0, 0.75, 0.75, 0.5)
		));
		boundingBoxesForDirections.put(EnumFacing.SOUTH, ImmutableList.of(
				new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.5), new AxisAlignedBB(0.25, 0.25, 0.5, 0.75, 0.75, 1.0)
		));
		boundingBoxesForDirections.put(EnumFacing.WEST, ImmutableList.of(
				new AxisAlignedBB(0.5, 0.0, 0.0, 1.0, 1.0, 1.0), new AxisAlignedBB(0.0, 0.25, 0.25, 0.5, 0.75, 0.75)
		));
		boundingBoxesForDirections.put(EnumFacing.EAST, ImmutableList.of(
				new AxisAlignedBB(0.0, 0.0, 0.0, 0.5, 1.0, 1.0), new AxisAlignedBB(0.5, 0.25, 0.25, 1.0, 0.75, 0.75)
		));
	}

	public BlockEngine() {
		super(BlockTypeEngine.class);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn) {
		TileEngine tile = TileUtil.getTile(worldIn, pos, TileEngine.class);
		if (tile == null) {
			super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn);
			return;
		}

		EnumFacing orientation = tile.getOrientation();
		List<AxisAlignedBB> boundingBoxes = boundingBoxesForDirections.get(orientation);
		if (boundingBoxes == null) {
			return;
		}

		for (AxisAlignedBB boundingBoxBase : boundingBoxes) {
			AxisAlignedBB boundingBox = boundingBoxBase.offset(pos.getX(), pos.getY(), pos.getZ());
			if (entityBox.intersectsWith(boundingBox)) {
				collidingBoxes.add(boundingBox);
			}
		}
	}

	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
		TileEngine tile = TileUtil.getTile(worldIn, pos, TileEngine.class);
		if (tile == null) {
			return super.collisionRayTrace(blockState, worldIn, pos, start, end);
		}

		EnumFacing orientation = tile.getOrientation();
		List<AxisAlignedBB> boundingBoxes = boundingBoxesForDirections.get(orientation);
		if (boundingBoxes == null) {
			return super.collisionRayTrace(blockState, worldIn, pos, start, end);
		}

		RayTraceResult nearestIntersection = null;
		for (AxisAlignedBB boundingBoxBase : boundingBoxes) {
			AxisAlignedBB boundingBox = boundingBoxBase.offset(pos.getX(), pos.getY(), pos.getZ());
			RayTraceResult intersection = boundingBox.calculateIntercept(start, end);
			if (intersection != null) {
				if (nearestIntersection == null || intersection.hitVec.distanceTo(start) < nearestIntersection.hitVec.distanceTo(start)) {
					nearestIntersection = intersection;
				}
			}
		}

		if (nearestIntersection != null) {
			Object hitInfo = nearestIntersection.hitInfo;
			Entity entityHit = nearestIntersection.entityHit;
			nearestIntersection = new RayTraceResult(nearestIntersection.typeOfHit, nearestIntersection.hitVec, nearestIntersection.sideHit, pos);
			nearestIntersection.hitInfo = hitInfo;
			nearestIntersection.entityHit = entityHit;
		}

		return nearestIntersection;
	}
}
