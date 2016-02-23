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

import java.util.List;

import forestry.api.core.IModelManager;
import forestry.core.tiles.TileForestry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IMachineProperties<T extends TileForestry> extends IStringSerializable{
	int getMeta();

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

	void getSubBlocks(@Nonnull Item item, @Nonnull CreativeTabs tab, @Nonnull List<ItemStack> list);

	@Nonnull
	AxisAlignedBB getBoundingBox(@Nonnull BlockPos pos, @Nonnull IBlockState state);

	@Nonnull
	MovingObjectPosition collisionRayTrace(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3 startVec, @Nonnull Vec3 endVec);

	boolean isSolidOnSide(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side);

	boolean rotateBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing axis);
}
