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
package forestry.greenhouse.multiblock.blocks.wall;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.core.IErrorState;
import forestry.greenhouse.api.greenhouse.IBlankBlock;
import forestry.greenhouse.api.greenhouse.IGreenhouseBlock;
import forestry.greenhouse.api.greenhouse.IGreenhouseBlockHandler;
import forestry.greenhouse.api.greenhouse.IGreenhouseBlockStorage;
import forestry.greenhouse.api.greenhouse.IGreenhouseProvider;
import forestry.greenhouse.api.greenhouse.IGreenhouseProviderListener;
import forestry.greenhouse.api.greenhouse.IWallBlock;

public class WallBlockHandler implements IGreenhouseBlockHandler<IWallBlock, IBlankBlock> {

	private static final WallBlockHandler INSTANCE = new WallBlockHandler();

	public static WallBlockHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public void onRemoveBlock(IGreenhouseBlockStorage storage, IWallBlock blockToRemove) {
		BlockPos pos = blockToRemove.getPos();
		blockToRemove.onRemove();
	}

	@Override
	public IWallBlock createBlock(IGreenhouseBlockStorage storage, @Nullable IBlankBlock root, @Nullable EnumFacing rootFacing, @Nullable BlockPos position) {
		IWallBlock wallBlock = new WallBlock(storage.getProvider(), position);
		wallBlock.setRoot(root);
		wallBlock.setRootFace(rootFacing);
		wallBlock.onCreate();
		return wallBlock;
	}

	@Override
	public IWallBlock getBlock(IGreenhouseBlockStorage storage, BlockPos position) {
		IGreenhouseBlock logicBlock = storage.getBlock(position);
		if (logicBlock instanceof IWallBlock) {
			return (IWallBlock) logicBlock;
		}
		return null;
	}

	@Override
	public IErrorState checkNeighborBlocks(IGreenhouseBlockStorage storage, IWallBlock blockToCheck, List newBlocks) {
		return null;
	}

	@Override
	public boolean onCheckPosition(IGreenhouseBlockStorage storage, IBlankBlock rootBlock, BlockPos position, EnumFacing facing, IGreenhouseBlock block, List<IGreenhouseBlock> newBlocksToCheck) {
		IGreenhouseProvider provider = storage.getProvider();
		if (block == null && isValidWallBlock(provider.getWorld(), position)) {
			for (IGreenhouseProviderListener listener : provider.getListeners()) {
				listener.onCheckPosition(position);
			}
			IWallBlock wallBlock = createBlock(storage, rootBlock, facing, position);
			storage.setBlock(position, wallBlock);

			rootBlock.setFaceTested(facing, true);
			rootBlock.setNearWall(true);
			return true;
		}
		return false;
	}

	@Override
	public Class<? extends IWallBlock> getBlockClass() {
		return IWallBlock.class;
	}

	private boolean isValidWallBlock(World world, BlockPos pos) {
		return !isAirBlock(world, pos);
	}

	/**
	 * Same as {@link World#isAirBlock(BlockPos)} but faster
	 */
	private boolean isAirBlock(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		AxisAlignedBB collisionBB = blockState.getCollisionBoundingBox(world, pos);
		return collisionBB == null || collisionBB.equals(Block.NULL_AABB) || blockState.getBlock().isLeaves(blockState, world, pos);
	}
}
