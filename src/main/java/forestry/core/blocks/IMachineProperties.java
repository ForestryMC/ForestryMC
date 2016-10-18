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

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import forestry.api.core.IModelManager;
import forestry.core.tiles.TileForestry;

public interface IMachineProperties<T extends TileForestry> extends IStringSerializable {
	@Nonnull
	String getTeIdent();

	@Nonnull
	Class<T> getTeClass();

	/**
	 * Registers the tile entity with MC.
	 */
	void registerTileEntity();

	void registerModel(Item item, IModelManager manager);

	@Nonnull
	TileEntity createTileEntity();

	void setBlock(@Nonnull Block block);

	Block getBlock();
	
	boolean isFullCube(IBlockState state);

	@Nonnull
	AxisAlignedBB getBoundingBox(@Nonnull BlockPos pos, @Nonnull IBlockState state);

	@Nonnull
	RayTraceResult collisionRayTrace(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d startVec, @Nonnull Vec3d endVec);
}
