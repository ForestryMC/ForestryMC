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
package forestry.core.tiles;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.minecraftforge.network.NetworkHooks;

import forestry.core.blocks.BlockBase;
import forestry.core.blocks.IBlockType;

public abstract class TileBase extends TileForestry {

	public TileBase(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
	}

	public void openGui(ServerPlayer player, BlockPos pos) {
		if (!hasGui()) {
			return;
		}
		NetworkHooks.openScreen(player, this, pos);
	}

	protected boolean hasGui() {
		return true;
	}

	@Override
	public String getUnlocalizedTitle() {
		Block block = getBlockState().getBlock();
		if (block instanceof BlockBase) {
			return block.getDescriptionId();
		}
		return super.getUnlocalizedTitle();
	}

	@SuppressWarnings("unchecked")
	public <T extends IBlockType> T getBlockType(T fallbackType) {
		BlockState blockState = getBlockState();
		Block block = blockState.getBlock();
		if (!(block instanceof BlockBase blockBase)) {
			return fallbackType;
		}
		return (T) blockBase.blockType;
	}


	//TODO
	//	@Override
	//	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState) {
	//		Block oldBlock = oldState.getBlock();
	//		Block newBlock = newState.getBlock();
	//		return oldBlock != newBlock || !(oldBlock instanceof BlockBase) || !(newBlock instanceof BlockBase);
	//	}

	@Nonnull
	public Direction getFacing() {
		return getLevel().getBlockState(getBlockPos()).getValue(BlockBase.FACING);
	}

}
