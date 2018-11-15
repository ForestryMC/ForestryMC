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

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import forestry.core.blocks.BlockBase;
import forestry.core.tiles.TileEngine;
import forestry.core.tiles.TileUtil;
import forestry.energy.EnergyHelper;
import forestry.energy.EnergyManager;

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

	public BlockEngine(BlockTypeEngine blockType) {
		super(blockType);
		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		EnumFacing orientation = state.getValue(FACING);
		List<AxisAlignedBB> boundingBoxes = boundingBoxesForDirections.get(orientation);
		if (boundingBoxes == null) {
			return;
		}

		for (AxisAlignedBB boundingBoxBase : boundingBoxes) {
			AxisAlignedBB boundingBox = boundingBoxBase.offset(pos.getX(), pos.getY(), pos.getZ());
			if (entityBox.intersects(boundingBox)) {
				collidingBoxes.add(boundingBox);
			}
		}
	}

	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
		EnumFacing orientation = blockState.getValue(FACING);
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

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		return rotate(world, pos) ||
			super.rotateBlock(world, pos, axis);
	}

	private static boolean isOrientedAtEnergyReciever(World world, BlockPos pos, EnumFacing orientation) {
		BlockPos offsetPos = pos.offset(orientation);
		TileEntity tile = TileUtil.getTile(world, offsetPos);
		return EnergyHelper.isEnergyReceiverOrEngine(orientation.getOpposite(), tile);
	}

	private static boolean rotate(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		EnumFacing blockFacing = blockState.getValue(FACING);
		for (int i = blockFacing.ordinal() + 1; i <= blockFacing.ordinal() + 6; ++i) {
			EnumFacing orientation = EnumFacing.values()[i % 6];
			if (isOrientedAtEnergyReciever(world, pos, orientation)) {
				blockState = blockState.withProperty(FACING, orientation);
				world.setBlockState(pos, blockState);
				return true;
			}
		}
		return false;
	}

	@Override
	public void rotateAfterPlacement(EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		EnumFacing orientation = side.getOpposite();
		if (isOrientedAtEnergyReciever(world, pos, orientation)) {
			IBlockState blockState = world.getBlockState(pos);
			blockState = blockState.withProperty(FACING, orientation);
			world.setBlockState(pos, blockState);
		} else {
			super.rotateAfterPlacement(player, world, pos, side);
			rotate(world, pos);
		}
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		IBlockState blockState = world.getBlockState(pos);
		EnumFacing facing = blockState.getValue(BlockBase.FACING);
		return facing.getOpposite() == side;
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		TileEngine tileEngine = TileUtil.getTile(worldIn, pos, TileEngine.class);
		if (tileEngine != null) {
			EnergyManager energyManager = tileEngine.getEnergyManager();
			return energyManager.calculateRedstone();
		}
		return 0;
	}
}
