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

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

@OnlyIn(Dist.CLIENT)
public class ModelBakerModel implements IBakedModel {

	private boolean isGui3d;
	private boolean isAmbientOcclusion;
	private TextureAtlasSprite particleSprite;
	@Nullable
	private IModelState modelState;
	@Nullable
	private ImmutableMap<TransformType, TRSRTransformation> transforms;

	private final Map<Direction, List<BakedQuad>> faceQuads;
	private final List<BakedQuad> generalQuads;
	private final List<Pair<BlockState, IBakedModel>> models;
	private final List<Pair<BlockState, IBakedModel>> modelsPost;

	private float[] rotation = getDefaultRotation();
	private float[] translation = getDefaultTranslation();
	private float[] scale = getDefaultScale();

	public ModelBakerModel(IModelState modelState) {
		models = new ArrayList<>();
		modelsPost = new ArrayList<>();
		faceQuads = new EnumMap<>(Direction.class);
		generalQuads = new ArrayList<>();
		particleSprite = Minecraft.getInstance().getTextureMap().missingImage;
		isGui3d = true;
		isAmbientOcclusion = false;
		setModelState(modelState);

		for (Direction face : Direction.VALUES) {
			faceQuads.put(face, new ArrayList<>());
		}
	}

	private ModelBakerModel(List<Pair<BlockState, IBakedModel>> models, List<Pair<BlockState, IBakedModel>> modelsPost, Map<Direction, List<BakedQuad>> faceQuads, List<BakedQuad> generalQuads, boolean isGui3d, boolean isAmbientOcclusion, IModelState modelState, float[] rotation, float[] translation, float[] scale, TextureAtlasSprite particleSprite) {
		this.models = models;
		this.modelsPost = modelsPost;
		this.faceQuads = faceQuads;
		this.generalQuads = generalQuads;
		this.isGui3d = isGui3d;
		this.isAmbientOcclusion = isAmbientOcclusion;
		this.rotation = rotation;
		this.translation = translation;
		this.scale = scale;
		this.particleSprite = particleSprite;
		setModelState(modelState);
	}

	@Override
	public boolean isGui3d() {
		return isGui3d;
	}

	public void setAmbientOcclusion(boolean ambientOcclusion) {
		this.isAmbientOcclusion = ambientOcclusion;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return isAmbientOcclusion;
	}

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
		return ItemOverrideList.EMPTY;
	}

	private static float[] getDefaultRotation() {
		return new float[]{-80, -45, 170};
	}

	private static float[] getDefaultTranslation() {
		return new float[]{0, 1.5F, -2.75F};
	}

	private static float[] getDefaultScale() {
		return new float[]{0.375F, 0.375F, 0.375F};
	}

	public void setRotation(float[] rotation) {
		this.rotation = rotation;
	}

	public void setTranslation(float[] translation) {
		this.translation = translation;
	}

	public void setScale(float[] scale) {
		this.scale = scale;
	}

	public float[] getRotation() {
		return rotation;
	}

	public float[] getTranslation() {
		return translation;
	}

	public float[] getScale() {
		return scale;
	}

	public void setModelState(@Nullable IModelState modelState) {
		this.modelState = modelState;
		this.transforms = PerspectiveMapWrapper.getTransforms(modelState);
	}

	public void addQuad(@Nullable Direction facing, BakedQuad quad) {
		if (facing != null) {
			faceQuads.get(facing).add(quad);
		} else {
			generalQuads.add(quad);
		}
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
		List<BakedQuad> quads = new ArrayList<>();
		for (Pair<BlockState, IBakedModel> model : this.models) {
			List<BakedQuad> modelQuads = model.getRight().getQuads(model.getLeft(), side, rand);
			if (!modelQuads.isEmpty()) {
				quads.addAll(modelQuads);
			}
		}
		if (side != null) {
			quads.addAll(faceQuads.get(side));
		}
		quads.addAll(generalQuads);
		for (Pair<BlockState, IBakedModel> model : this.modelsPost) {
			List<BakedQuad> modelQuads = model.getRight().getQuads(model.getLeft(), side, rand);
			if (!modelQuads.isEmpty()) {
				quads.addAll(modelQuads);
			}
		}
		return quads;
	}

	public ModelBakerModel copy() {
		return new ModelBakerModel(models, modelsPost, faceQuads, generalQuads, isGui3d, isAmbientOcclusion, modelState, rotation, translation, scale, particleSprite);
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		return PerspectiveMapWrapper.handlePerspective(this, transforms, cameraTransformType);
	}
}
