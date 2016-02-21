/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;

/**
 * A model baker to make custom models in the mod
 */
public interface IModelBaker {

	void setRenderBoundsFromBlock(Block block);

	void setRenderBounds(double d, double e, double f, double g, double h, double i );

	void setColorIndex(int color);

	boolean renderStandardBlock(Block block, BlockPos pos, TextureAtlasSprite[] sprites, int colorIndex);
	
	boolean renderStandardBlock(Block block, BlockPos pos, TextureAtlasSprite sprites, int colorIndex);
	
	void renderFaceXNeg(BlockPos pos, TextureAtlasSprite sprite);
	
	void renderFaceYNeg(BlockPos pos, TextureAtlasSprite sprite);

	void renderFaceZNeg(BlockPos pos, TextureAtlasSprite sprite);

    void renderFaceYPos(BlockPos pos, TextureAtlasSprite sprite);

	void renderFaceZPos(BlockPos pos, TextureAtlasSprite sprite);

	void renderFaceXPos(BlockPos pos, TextureAtlasSprite sprite);
	
	IBakedModel bakeModel(boolean flip);
	
}
