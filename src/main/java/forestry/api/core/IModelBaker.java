/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A model baker to make custom models
 */
@SideOnly(Side.CLIENT)
public interface IModelBaker {

	void setRenderBoundsFromBlock(Block block);

	void setRenderBounds(double d, double e, double f, double g, double h, double i );

	void setColorIndex(int color);

	void addBlockModel(Block block, BlockPos pos, TextureAtlasSprite[] sprites, int colorIndex);
	
	void addBlockModel(Block block, BlockPos pos, TextureAtlasSprite sprites, int colorIndex);
	
	void addFaceXNeg(BlockPos pos, TextureAtlasSprite sprite);
	
	void addFaceYNeg(BlockPos pos, TextureAtlasSprite sprite);

	void addFaceZNeg(BlockPos pos, TextureAtlasSprite sprite);

    void addFaceYPos(BlockPos pos, TextureAtlasSprite sprite);

	void addFaceZPos(BlockPos pos, TextureAtlasSprite sprite);

	void addFaceXPos(BlockPos pos, TextureAtlasSprite sprite);
	
	IModelBakerModel getCurrentModel();
	
	IModelBakerModel bakeModel(boolean flip);
	
	IModelBakerModel clear();
	
}
