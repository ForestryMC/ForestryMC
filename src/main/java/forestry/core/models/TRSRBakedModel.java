/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2013-2014 Slime Knights (mDiyo, fuj1n, Sunstrike, progwml6, pillbox, alexbegt)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Any alternate licenses are noted where appropriate.
 ******************************************************************************/
package forestry.core.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.model.TRSRTransformation;
// for those wondering TRSR stands for Translation Rotation Scale Rotation
@OnlyIn(Dist.CLIENT)
public class TRSRBakedModel implements IBakedModel {

	protected final ImmutableList<BakedQuad> general;
	protected final ImmutableMap<Direction, ImmutableList<BakedQuad>> faces;
	protected final IBakedModel original;

	public TRSRBakedModel(IBakedModel original, float x, float y, float z, float scale) {
		this(original, x, y, z, 0, 0, 0, scale, scale, scale);
	}

	public TRSRBakedModel(IBakedModel original, float x, float y, float z, float rotX, float rotY, float rotZ, float scale) {
		this(original, x, y, z, rotX, rotY, rotZ, scale, scale, scale);
	}

	public TRSRBakedModel(IBakedModel original, float x, float y, float z, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ) {
		this(original, new TRSRTransformation(new Vector3f(x, y, z),
			null,
			new Vector3f(scaleX, scaleY, scaleZ),
			TRSRTransformation.quatFromYXZ(rotY, rotX, rotZ)));
	}

	public TRSRBakedModel(IBakedModel original, TRSRTransformation transform) {
		this.original = original;

		ImmutableList.Builder<BakedQuad> builder;
		builder = ImmutableList.builder();

		transform = TRSRTransformation.blockCenterToCorner(transform);

		// face quads
		EnumMap<Direction, ImmutableList<BakedQuad>> faces = Maps.newEnumMap(Direction.class);
		for (Direction face : Direction.values()) {
			if (!original.isBuiltInRenderer()) {
				for (BakedQuad quad : original.getQuads(null, face, new Random())) {
					Transformer transformer = new Transformer(transform, quad.getFormat());
					quad.pipe(transformer);
					builder.add(transformer.build());
				}
			}
			//faces.put(face, builder.build());
			faces.put(face, ImmutableList.of());
		}

		// general quads
		//builder = ImmutableList.builder();
		if (!original.isBuiltInRenderer()) {
			for (BakedQuad quad : original.getQuads(null, null, new Random())) {
				Transformer transformer = new Transformer(transform, quad.getFormat());
				quad.pipe(transformer);
				builder.add(transformer.build());
			}
		}

		this.general = builder.build();
		this.faces = Maps.immutableEnumMap(faces);
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
		if (side != null) {
			return faces.get(side);
		}
		return general;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return original.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return original.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return original.getParticleTexture();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return original.getItemCameraTransforms();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return original.getOverrides();
	}

	public static class Transformer extends VertexTransformer {

		protected final Matrix4f transformation;
		protected final Matrix3f normalTransformation;

		public Transformer(TRSRTransformation transformation, VertexFormat format) {
			super(new UnpackedBakedQuad.Builder(format));
			// position transform
			this.transformation = TRSRTransformation.getMatrix(Direction.UP/*this.parent.getVertexFormat()*/);    //TODO how to access direction
			// normal transform
			this.normalTransformation = new Matrix3f();
			this.transformation.getRotationScale(this.normalTransformation);
			this.normalTransformation.invert();
			this.normalTransformation.transpose();
		}

		@Override
		public void put(int element, float... data) {
			VertexFormatElement.Usage usage = parent.getVertexFormat().getElement(element).getUsage();

			// transform normals and position
			if (usage == VertexFormatElement.Usage.POSITION && data.length >= 3) {
				Vector4f vec = new Vector4f(data);
				vec.setW(1.0f);
				transformation.transform(vec);
				data = new float[4];
				vec.get(data);
			} else if (usage == VertexFormatElement.Usage.NORMAL && data.length >= 3) {
				Vector3f vec = new Vector3f(data);
				normalTransformation.transform(vec);
				vec.normalize();
				data = new float[4];
				vec.get(data);
			}
			super.put(element, data);
		}

		public UnpackedBakedQuad build() {
			return ((UnpackedBakedQuad.Builder) parent).build();
		}
	}
}