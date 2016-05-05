/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IModelBakerModel extends IBakedModel {
	
	void setGui3d(boolean gui3d);
	
	void setAmbientOcclusion(boolean ambientOcclusion);
	
	void setParticleSprite(TextureAtlasSprite particleSprite);
	
//	void setFormat(VertexFormat formate);
	
    void addQuad(EnumFacing facing, BakedQuad quad);
	
	void setRotation(float[] rotation);

	void setTranslation(float[] translation);

	void setScale(float[] scale);
	
	float[] getRotation();

	float[] getTranslation();

	float[] getScale();

}
