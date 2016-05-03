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
package forestry.core.models.baker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IModelBakerModel;

@SideOnly(Side.CLIENT)
public class ModelBakerModel implements IModelBakerModel {
	private final List<IBakedModel> models;
	private boolean isGui3d;
	private boolean isAmbientOcclusion;
	private VertexFormat format;
	private TextureAtlasSprite particleSprite;
	private float[] rotation = getDefaultRotation();
	private float[] translation = getDefaultTranslation();
	private float[] scale = getDefaultScale();

	public ModelBakerModel() {
		models = new ArrayList<>();
		format = DefaultVertexFormats.BLOCK;
		isGui3d = true;
		isAmbientOcclusion = false;
	}

	private ModelBakerModel(List<IBakedModel> models, boolean isGui3d, boolean isAmbientOcclusion, VertexFormat format, float[] rotation, float[] translation, float[] scale, TextureAtlasSprite particleSprite) {
		this.models = models;
		this.isGui3d = isGui3d;
		this.isAmbientOcclusion = isAmbientOcclusion;
		this.format = format;
		this.rotation = rotation;
		this.translation = translation;
		this.scale = scale;
		this.particleSprite = particleSprite;
	}
	
	@Override
	public void setGui3d(boolean gui3d) {
		this.isGui3d = gui3d;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public void setAmbientOcclusion(boolean ambientOcclusion) {
		this.isAmbientOcclusion = ambientOcclusion;
	}
	
	@Override
	public boolean isAmbientOcclusion() {
		return isAmbientOcclusion;
	}
	
	@Override
	public void setParticleSprite(TextureAtlasSprite particleSprite) {
		this.particleSprite = particleSprite;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return particleSprite;
	}
	
	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}

	public static float[] getDefaultRotation() {
		return new float[] { -80, -45, 170 };
	}

	public static float[] getDefaultTranslation() {
		return new float[] { 0, 1.5F, -2.75F };
	}

	public static float[] getDefaultScale() {
		return new float[] { 0.375F, 0.375F, 0.375F };
	}
	
	@Override
	public void setRotation(float[] rotation) {
		this.rotation = rotation;
	}
	
	@Override
	public void setTranslation(float[] translation) {
		this.translation = translation;
	}
	
	@Override
	public void setScale(float[] scale) {
		this.scale = scale;
	}
	
	@Override
	public float[] getRotation() {
		return rotation;
	}

	@Override
	public float[] getTranslation() {
		return translation;
	}

	@Override
	public float[] getScale() {
		return scale;
	}

	public void addModelQuads(IBakedModel model) {
		this.models.add(model);
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		List<BakedQuad> quads = new ArrayList<>();
		if (side == null) {
			for (IBakedModel model : this.models) {
				quads.addAll(model.getQuads(state, null, rand));
			}
		} else {
			for (IBakedModel model : this.models) {
				quads.addAll(model.getQuads(state, side, rand));
			}
		}
		return quads;
	}

	public ModelBakerModel copy() {
		return new ModelBakerModel(models, isGui3d, isAmbientOcclusion, format, rotation, translation, scale, particleSprite);
	}
}
