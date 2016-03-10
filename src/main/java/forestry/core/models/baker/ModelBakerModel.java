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
import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.util.vector.Vector3f;

import forestry.api.core.IModelBakerModel;

@SideOnly(Side.CLIENT)
public class ModelBakerModel implements IModelBakerModel {
	private List<BakedQuad>[] faces = new List[6];
	private List<BakedQuad> general;
	private boolean isGui3d;
	private boolean isAmbientOcclusion;
	private VertexFormat formate;
	private TextureAtlasSprite particleSprite;
	private float[] rotation = getDefaultRotation();
	private float[] translation = getDefaultTranslation();
	private float[] scale = getDefaultScale();

	public ModelBakerModel() {
		general = new ArrayList<BakedQuad>();
		for (EnumFacing f : EnumFacing.VALUES)
			faces[f.ordinal()] = new ArrayList<BakedQuad>();
		formate = DefaultVertexFormats.BLOCK;
		isGui3d = true;
		isAmbientOcclusion = false;
	}

	private ModelBakerModel(List<BakedQuad> general, List<BakedQuad>[] faces, boolean isGui3d, boolean isAmbientOcclusion, VertexFormat formate, float[] rotation, float[] translation, float[] scale, TextureAtlasSprite particleSprite) {
		this.general = general;
		this.faces = faces;
		this.isGui3d = isGui3d;
		this.isAmbientOcclusion = isAmbientOcclusion;
		this.formate = formate;
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
	public VertexFormat getFormat() {
		return formate;
	}
	
	@Override
	public void setFormat(VertexFormat formate) {
		this.formate = formate;
	}
	
	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return getTransform();
	}

	public ItemCameraTransforms getTransform() {
		Vector3f rotation = new Vector3f(getRotation()[0], getRotation()[1], getRotation()[2]);
		Vector3f translation = new Vector3f(getTranslation()[0], getTranslation()[1], getTranslation()[2]);
		translation.scale(0.0625F);
		MathHelper.clamp_double(translation.x, -1.5D, 1.5D);
		MathHelper.clamp_double(translation.y, -1.5D, 1.5D);
		MathHelper.clamp_double(translation.z, -1.5D, 1.5D);
		Vector3f scale = new Vector3f(getScale()[0], getScale()[1], getScale()[2]);
		MathHelper.clamp_double(scale.x, -1.5D, 1.5D);
		MathHelper.clamp_double(scale.y, -1.5D, 1.5D);
		MathHelper.clamp_double(scale.z, -1.5D, 1.5D);

		new ItemTransformVec3f(rotation, translation, scale);
		ItemTransformVec3f transformV = new ItemTransformVec3f(rotation, translation, scale);
		return new ItemCameraTransforms(transformV, ItemCameraTransforms.DEFAULT.firstPerson, ItemCameraTransforms.DEFAULT.head, ItemCameraTransforms.DEFAULT.gui, ItemCameraTransforms.DEFAULT.ground, ItemCameraTransforms.DEFAULT.fixed);
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

	@Override
	public List<BakedQuad> getGeneralQuads() {
		return general;
	}

	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing face) {
		return faces[face.ordinal()];
	}

	public ModelBakerModel copy() {
		return new ModelBakerModel(general, faces, isGui3d, isAmbientOcclusion, formate, rotation, translation, scale, particleSprite);
	}
}
