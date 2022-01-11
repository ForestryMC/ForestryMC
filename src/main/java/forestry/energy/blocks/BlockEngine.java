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

import javax.annotation.Nullable;
import java.util.EnumMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import forestry.core.config.Preference;
import net.minecraftforge.common.ToolType;

import forestry.api.core.ForestryAPI;
import forestry.core.blocks.BlockBase;
import forestry.core.tiles.TileUtil;
import forestry.energy.EnergyHelper;
import forestry.energy.EnergyManager;
import forestry.energy.tiles.TileEngine;

public class BlockEngine extends BlockBase<BlockTypeEngine> {
	private static final EnumMap<Direction, VoxelShape> SHAPE_FOR_DIRECTIONS = new EnumMap<>(Direction.class);

	static {
		SHAPE_FOR_DIRECTIONS.put(Direction.EAST, VoxelShapes.or(Block.box(0, 0, 0, 6, 16, 16), Block.box(6, 2, 2, 10, 14, 14), Block.box(10, 4, 4, 16, 12, 12)));
		SHAPE_FOR_DIRECTIONS.put(Direction.WEST, VoxelShapes.or(Block.box(0, 4, 4, 6, 12, 12), Block.box(6, 2, 2, 10, 14, 14), Block.box(10, 0, 0, 16, 16, 16)));
		SHAPE_FOR_DIRECTIONS.put(Direction.SOUTH, VoxelShapes.or(Block.box(0, 0, 0, 16, 16, 6), Block.box(2, 2, 6, 14, 14, 10), Block.box(4, 4, 10, 12, 12, 16)));
		SHAPE_FOR_DIRECTIONS.put(Direction.NORTH, VoxelShapes.or(Block.box(4, 4, 0, 12, 12, 6), Block.box(2, 2, 6, 14, 14, 10), Block.box(0, 0, 10, 16, 16, 16)));
		SHAPE_FOR_DIRECTIONS.put(Direction.UP, VoxelShapes.or(Block.box(0, 0, 0, 16, 6, 16), Block.box(2, 6, 2, 14, 10, 14), Block.box(4, 10, 4, 12, 16, 12)));
		SHAPE_FOR_DIRECTIONS.put(Direction.DOWN, VoxelShapes.or(Block.box(0, 10, 0, 16, 16, 16), Block.box(2, 6, 2, 14, 10, 14), Block.box(4, 0, 4, 12, 6, 12)));
	}

	public BlockEngine(BlockTypeEngine blockType) {
		super(blockType, Properties.of(Material.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(0));
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> stacks) {
		if (blockType == BlockTypeEngine.CLOCKWORK && !Preference.CLOCKWORK_ENGINE) {
			return;
		}

		super.fillItemCategory(group, stacks);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
		Direction orientation = state.getValue(FACING);
		return SHAPE_FOR_DIRECTIONS.get(orientation);
	}

	//TODO raytracing
	//	@Override
	//	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
	//		Direction orientation = state.get(FACING);
	//		List<AxisAlignedBB> boundingBoxes = boundingBoxesForDirections.get(orientation);
	//		if (boundingBoxes == null) {
	//			return;
	//		}
	//
	//		for (AxisAlignedBB boundingBoxBase : boundingBoxes) {
	//			AxisAlignedBB boundingBox = boundingBoxBase.offset(pos.getX(), pos.getY(), pos.getZ());
	//			if (entityBox.intersects(boundingBox)) {
	//				collidingBoxes.add(boundingBox);
	//			}
	//		}
	//	}

	//TODO potentially getRayTraceResult
	//	@Override
	//	public RayTraceResult collisionRayTrace(BlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
	//		Direction orientation = blockState.get(FACING);
	//		List<AxisAlignedBB> boundingBoxes = boundingBoxesForDirections.get(orientation);
	//		if (boundingBoxes == null) {
	//			return super.collisionRayTrace(blockState, worldIn, pos, start, end);
	//		}
	//
	//		RayTraceResult nearestIntersection = null;
	//		for (AxisAlignedBB boundingBoxBase : boundingBoxes) {
	//			AxisAlignedBB boundingBox = boundingBoxBase.offset(pos.getX(), pos.getY(), pos.getZ());
	//			RayTraceResult intersection = boundingBox.calculateIntercept(start, end);
	//			if (intersection != null) {
	//				if (nearestIntersection == null || intersection.hitVec.distanceTo(start) < nearestIntersection.hitVec.distanceTo(start)) {
	//					nearestIntersection = intersection;
	//				}
	//			}
	//		}
	//
	//		if (nearestIntersection != null) {
	//			//TODO needs to be entityRayTrace here
	//			Object hitInfo = nearestIntersection.hitInfo;
	//			Entity entityHit = nearestIntersection.entityHit;
	//			nearestIntersection = new RayTraceResult(nearestIntersection.typeOfHit, nearestIntersection.hitVec, nearestIntersection.sideHit, pos);
	//			nearestIntersection.hitInfo = hitInfo;
	//			nearestIntersection.entityHit = entityHit;
	//		}
	//
	//		return nearestIntersection;
	//	}

	@Override
	public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation rot) {
		return rotate(state, world, pos);    //TODO check
	}

	private static boolean isOrientedAtEnergyReciever(IWorld world, BlockPos pos, Direction orientation) {
		BlockPos offsetPos = pos.relative(orientation);
		TileEntity tile = TileUtil.getTile(world, offsetPos);
		return EnergyHelper.isEnergyReceiverOrEngine(orientation.getOpposite(), tile);
	}

	private static BlockState rotate(BlockState state, IWorld world, BlockPos pos) {
		Direction blockFacing = state.getValue(FACING);
		for (int i = blockFacing.ordinal() + 1; i <= blockFacing.ordinal() + 6; ++i) {
			Direction orientation = Direction.values()[i % 6];
			if (isOrientedAtEnergyReciever(world, pos, orientation)) {
				return state.setValue(FACING, orientation);
			}
		}
		return state;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction orientation = context.getClickedFace().getOpposite();
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (isOrientedAtEnergyReciever(world, pos, orientation)) {
			return defaultBlockState().setValue(FACING, orientation);
		}
		return rotate(defaultBlockState().setValue(FACING, context.getHorizontalDirection()), world, pos);
	}

	//TODO voxelShapes?
	//	@Override
	//	public boolean isSideSolid(BlockState base_state, IBlockReader world, BlockPos pos, Direction side) {
	//		BlockState blockState = world.getBlockState(pos);
	//		Direction facing = blockState.get(BlockBase.FACING);
	//		return facing.getOpposite() == side;
	//	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
		TileEngine tileEngine = TileUtil.getTile(worldIn, pos, TileEngine.class);
		if (tileEngine != null) {
			EnergyManager energyManager = tileEngine.getEnergyManager();
			return energyManager.calculateRedstone();
		}
		return 0;
	}
}
