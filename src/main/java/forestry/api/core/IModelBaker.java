/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

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

	void setRenderBounds(AxisAlignedBB renderBounds);

	void setRenderBounds(double d, double e, double f, double g, double h, double i);

	void setColorIndex(int color);

	void addBlockModel(Block block, AxisAlignedBB renderBounds, @Nullable BlockPos pos, TextureAtlasSprite[] sprites, int colorIndex);

	void addBlockModel(Block block, AxisAlignedBB renderBounds, @Nullable BlockPos pos, TextureAtlasSprite sprites, int colorIndex);

	void addModel(AxisAlignedBB renderBounds, TextureAtlasSprite[] textures, int colorIndex);

	void addModel(AxisAlignedBB renderBounds, TextureAtlasSprite texture, int colorIndex);

	void addBakedModel(@Nullable IBlockState state, IBakedModel model);

	void addFace(EnumFacing facing, TextureAtlasSprite sprite);

	void setModelState(@Nullable IModelState modelState);

	void setParticleSprite(TextureAtlasSprite particleSprite);

	IModelBakerModel bakeModel(boolean flip);

}
