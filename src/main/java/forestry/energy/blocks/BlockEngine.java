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

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.blocks.BlockBase;
import forestry.core.tiles.TileEngine;
import forestry.core.tiles.TileUtil;

public class BlockEngine extends BlockBase<BlockEngineType> {
	private static final EnumMap<ForgeDirection, List<AxisAlignedBB>> boundingBoxesForDirections = new EnumMap<>(ForgeDirection.class);

	static {
		boundingBoxesForDirections.put(ForgeDirection.DOWN, ImmutableList.of(
				AxisAlignedBB.getBoundingBox(0.0, 0.5, 0.0, 1.0, 1.0, 1.0), AxisAlignedBB.getBoundingBox(0.25, 0.0, 0.25, 0.75, 0.5, 0.75)
		));
		boundingBoxesForDirections.put(ForgeDirection.UP, ImmutableList.of(
				AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 1.0, 0.5, 1.0), AxisAlignedBB.getBoundingBox(0.25, 0.5, 0.25, 0.75, 1.0, 0.75)
		));
		boundingBoxesForDirections.put(ForgeDirection.NORTH, ImmutableList.of(
				AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.5, 1.0, 1.0, 1.0), AxisAlignedBB.getBoundingBox(0.25, 0.25, 0.0, 0.75, 0.75, 0.5)
		));
		boundingBoxesForDirections.put(ForgeDirection.SOUTH, ImmutableList.of(
				AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 1.0, 1.0, 0.5), AxisAlignedBB.getBoundingBox(0.25, 0.25, 0.5, 0.75, 0.75, 1.0)
		));
		boundingBoxesForDirections.put(ForgeDirection.WEST, ImmutableList.of(
				AxisAlignedBB.getBoundingBox(0.5, 0.0, 0.0, 1.0, 1.0, 1.0), AxisAlignedBB.getBoundingBox(0.0, 0.25, 0.25, 0.5, 0.75, 0.75)
		));
		boundingBoxesForDirections.put(ForgeDirection.EAST, ImmutableList.of(
				AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 0.5, 1.0, 1.0), AxisAlignedBB.getBoundingBox(0.5, 0.25, 0.25, 1.0, 0.75, 0.75)
		));
	}

	public BlockEngine() {
		super(true);
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
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {
		TileEngine tile = TileUtil.getTile(world, x, y, z, TileEngine.class);
		if (tile == null) {
			return super.collisionRayTrace(world, x, y, z, origin, direction);
		}

		ForgeDirection orientation = tile.getOrientation();
		List<AxisAlignedBB> boundingBoxes = boundingBoxesForDirections.get(orientation);
		if (boundingBoxes == null) {
			return super.collisionRayTrace(world, x, y, z, origin, direction);
		}

		MovingObjectPosition nearestIntersection = null;
		for (AxisAlignedBB boundingBoxBase : boundingBoxes) {
			AxisAlignedBB boundingBox = boundingBoxBase.getOffsetBoundingBox(x, y, z);
			MovingObjectPosition intersection = boundingBox.calculateIntercept(origin, direction);
			if (intersection != null) {
				if (nearestIntersection == null || (intersection.hitVec.distanceTo(origin) < nearestIntersection.hitVec.distanceTo(origin))) {
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
