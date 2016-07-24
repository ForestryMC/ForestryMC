/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A model baker to make custom models
 */
@SideOnly(Side.CLIENT)
public interface IModelBaker {

	void setRenderBounds(@Nonnull AxisAlignedBB renderBounds);

	void setRenderBounds(double d, double e, double f, double g, double h, double i);

	void setColorIndex(int color);

	void addBlockModel(@Nonnull Block block, @Nonnull AxisAlignedBB renderBounds, @Nullable BlockPos pos, @Nonnull TextureAtlasSprite[] sprites, int colorIndex);
	
	void addBlockModel(@Nonnull Block block, @Nonnull AxisAlignedBB renderBounds, @Nullable BlockPos pos, @Nonnull TextureAtlasSprite sprites, int colorIndex);
	
	void addModel(@Nonnull AxisAlignedBB renderBounds, @Nonnull TextureAtlasSprite[] textures, int colorIndex);
	
	void addModel(@Nonnull AxisAlignedBB renderBounds, @Nonnull TextureAtlasSprite texture, int colorIndex);
	
	void addBakedModel(@Nullable IBlockState state, @Nonnull IBakedModel model);
	
	void addFace(@Nonnull EnumFacing facing, @Nonnull TextureAtlasSprite sprite);
	
	void setModelState(@Nullable IModelState modelState);
	
	void setParticleSprite(@Nullable TextureAtlasSprite particleSprite);
	
	IModelBakerModel bakeModel(boolean flip);
	
}
