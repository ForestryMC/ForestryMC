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
package forestry.greenhouse.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ICamouflageHandler;

public interface IBlockCamouflaged<B extends Block & IBlockCamouflaged<B>> {

	ItemStack getCamouflageBlock(@Nullable IBlockAccess world, @Nullable BlockPos pos);

	@Nullable
	ICamouflageHandler getCamouflageHandler(@Nullable IBlockAccess world, @Nullable BlockPos pos);

	@SideOnly(Side.CLIENT)
	boolean hasOverlaySprite(int meta, int layer);

	int getLayers();

	@SideOnly(Side.CLIENT)
	TextureAtlasSprite getDefaultSprite();

	@SideOnly(Side.CLIENT)
	TextureAtlasSprite getOverlaySprite(@Nullable EnumFacing facing, @Nullable IBlockState state, int meta, int layer);
}
