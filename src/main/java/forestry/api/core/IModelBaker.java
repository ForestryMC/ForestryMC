/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.common.model.IModelState;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
/**
 * A model baker to make custom models
 *
 * @deprecated TODO Remove in 1.13: Not needed in the api
 */
@Deprecated
@SideOnly(Side.CLIENT)
public interface IModelBaker {

	void setColorIndex(int color);

	void addBlockModel(@Nullable BlockPos pos, TextureAtlasSprite[] sprites, int colorIndex);

	void addBlockModel(@Nullable BlockPos pos, TextureAtlasSprite sprites, int colorIndex);

	void addModel(TextureAtlasSprite[] textures, int colorIndex);

	void addModel(TextureAtlasSprite texture, int colorIndex);

	void addBakedModel(@Nullable IBlockState state, IBakedModel model);

	void addBakedModelPost(@Nullable IBlockState state, IBakedModel model);

	void addFace(EnumFacing facing, TextureAtlasSprite sprite);

	void setModelState(@Nullable IModelState modelState);

	void setParticleSprite(TextureAtlasSprite particleSprite);

	IModelBakerModel bakeModel(boolean flip);

}
