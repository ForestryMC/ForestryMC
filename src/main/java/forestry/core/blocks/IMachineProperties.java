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
package forestry.core.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import forestry.core.tiles.TileForestry;

public interface IMachineProperties<T extends TileForestry> extends IStringSerializable {
	Class<T> getTeClass();

	/**
	 * Registers the tile entity with MC.
	 */
	void registerTileEntity();

	void clientSetup();

	TileEntity createTileEntity();

	void setBlock(Block block);

	@Nullable
	Block getBlock();

	boolean isFullCube(BlockState state);

	VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context);
}
