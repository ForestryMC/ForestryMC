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

import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.network.NetworkHooks;

import forestry.core.blocks.BlockBase;

public abstract class TileBase extends TileForestry {

	public TileBase(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	public void openGui(ServerPlayerEntity player, BlockPos pos) {
		NetworkHooks.openGui(player, this, pos);
	}

	@Override
	public String getUnlocalizedTitle() {
		Block block = getBlockState().getBlock();
		if (block instanceof BlockBase) {
			return block.getTranslationKey();
		}
		return super.getUnlocalizedTitle();
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
		return getWorld().getBlockState(getPos()).get(BlockBase.FACING);
	}

}
