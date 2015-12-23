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
package forestry.core.render.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import forestry.api.core.IModelBaker;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IColoredBakedQuad;

public class ModelBaker implements IModelBaker {
	protected static class CachedModel implements IBakedModel {
		protected List<BakedQuad>[] faces = new List[6];
		protected List<BakedQuad> general;

		public CachedModel() {
			general = new ArrayList<BakedQuad>();
			for (EnumFacing f : EnumFacing.VALUES)
				faces[f.ordinal()] = new ArrayList<BakedQuad>();
		}

		private CachedModel(List<BakedQuad> general, List<BakedQuad>[] faces) {
			this.general = general;
			this.faces = faces;
		}

		@Override
		public boolean isGui3d() {
			return true;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public boolean isAmbientOcclusion() {
			return true;
		}

		@Override
		public TextureAtlasSprite getTexture() {
			return null;
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
			return new ItemCameraTransforms(transformV, ItemCameraTransforms.DEFAULT.firstPerson, ItemCameraTransforms.DEFAULT.head, ItemCameraTransforms.DEFAULT.gui, ItemCameraTransforms.DEFAULT.field_181699_o, ItemCameraTransforms.DEFAULT.field_181700_p);
		}

		protected float[] getRotation() {
			return new float[] { -80, -45, 170 };
		}

		protected float[] getTranslation() {
			return new float[] { 0, 1.5F, -2.75F };
		}

		protected float[] getScale() {
			return new float[] { 0.375F, 0.375F, 0.375F };
		}

		@Override
		public List getGeneralQuads() {
			return general;
		}

		@Override
		public List getFaceQuads(EnumFacing face) {
			return faces[face.ordinal()];
		}

		public CachedModel copy() {
			return new CachedModel(general, faces);
		}
	}
	
	protected static class ModelFace {

		public final EnumFacing face;
		public final boolean isEdge;

		public final Vector3f to;
		public final Vector3f from;

		public final float[] uv;

		public final TextureAtlasSprite spite;

		public final int color;

		public ModelFace(EnumFacing face, boolean isEdge, int color, Vector3f to, Vector3f from, float[] defUVs2,
				TextureAtlasSprite iconUnwrapper) {
			this.color = color;
			this.face = face;
			this.isEdge = isEdge;
			this.to = to;
			this.from = from;
			this.uv = defUVs2;
			this.spite = iconUnwrapper;
		}

	}

	protected double renderMinX;
	protected double renderMaxX;

	protected double renderMinY;
	protected double renderMaxY;

	protected double renderMinZ;
	protected double renderMaxZ;

	protected CachedModel generatedModel = new CachedModel();

	protected final FaceBakery faceBakery = new FaceBakery();

	protected float tx = 0, ty = 0, tz = 0;
	protected final float[] defUVs = new float[] { 0, 0, 1, 1 };

	@Override
	public void setRenderBoundsFromBlock(Block block) {
		if (block == null)
			return;

		renderMinX = block.getBlockBoundsMinX();
		renderMinY = block.getBlockBoundsMinY();
		renderMinZ = block.getBlockBoundsMinZ();
		renderMaxX = block.getBlockBoundsMaxX();
		renderMaxY = block.getBlockBoundsMaxY();
		renderMaxZ = block.getBlockBoundsMaxZ();
	}

	@Override
	public void setRenderBounds(double d, double e, double f, double g, double h, double i) {
		renderMinX = d;
		renderMinY = e;
		renderMinZ = f;
		renderMaxX = g;
		renderMaxY = h;
		renderMaxZ = i;
	}

	protected int color = -1;

	@Override
	public void setColor(int color) {
		this.color = color;
	}
	
	@Override
	public boolean renderStandardBlock(Block block, BlockPos pos, TextureAtlasSprite[] textures) {
		setRenderBoundsFromBlock(block);

		setColor(0xffffff);

		renderFaceXNeg(pos, textures[EnumFacing.WEST.ordinal()]);
		renderFaceXPos(pos, textures[EnumFacing.EAST.ordinal()]);
		renderFaceYNeg(pos, textures[EnumFacing.DOWN.ordinal()]);
		renderFaceYPos(pos, textures[EnumFacing.UP.ordinal()]);
		renderFaceZNeg(pos, textures[EnumFacing.NORTH.ordinal()]);
		renderFaceZPos(pos, textures[EnumFacing.SOUTH.ordinal()]);

		return false;
	}

	@Override
	public boolean renderStandardBlock(Block block, BlockPos pos, TextureAtlasSprite texture) {
		setRenderBoundsFromBlock(block);

		setColor(0xffffff);

		renderFaceXNeg(pos, texture);
		renderFaceXPos(pos, texture);
		renderFaceYNeg(pos, texture);
		renderFaceYPos(pos, texture);
		renderFaceZNeg(pos, texture);
		renderFaceZPos(pos, texture);

		return false;
	}

	@Override
	public void setTranslation(int x, int y, int z) {
		tx = x;
		ty = y;
		tz = z;
	}

	@Override
	public boolean isAlphaPass() {
		return MinecraftForgeClient.getRenderLayer() == EnumWorldBlockLayer.TRANSLUCENT;
	}

	final float quadsUV[] = new float[] { 0, 0, 1, 1, 0, 0, 1, 1 };
	public EnumSet<EnumFacing> renderFaces = EnumSet.allOf(EnumFacing.class);
	public boolean flipTexture = false;
	private List<ModelFace> faces = new ArrayList();

	protected float[] getFaceUvs(final EnumFacing face, final Vector3f to_16, final Vector3f from_16) {
		float from_a = 0;
		float from_b = 0;
		float to_a = 0;
		float to_b = 0;

		switch (face) {
		case UP:
			from_a = from_16.x / 16.0f;
			from_b = from_16.z / 16.0f;
			to_a = to_16.x / 16.0f;
			to_b = to_16.z / 16.0f;
			break;
		case DOWN:
			from_a = from_16.x / 16.0f;
			from_b = from_16.z / 16.0f;
			to_a = to_16.x / 16.0f;
			to_b = to_16.z / 16.0f;
			break;
		case SOUTH:
			from_a = from_16.x / 16.0f;
			from_b = from_16.y / 16.0f;
			to_a = to_16.x / 16.0f;
			to_b = to_16.y / 16.0f;
			break;
		case NORTH:
			from_a = from_16.x / 16.0f;
			from_b = from_16.y / 16.0f;
			to_a = to_16.x / 16.0f;
			to_b = to_16.y / 16.0f;
			break;
		case EAST:
			from_a = from_16.y / 16.0f;
			from_b = from_16.z / 16.0f;
			to_a = to_16.y / 16.0f;
			to_b = to_16.z / 16.0f;
			break;
		case WEST:
			from_a = from_16.y / 16.0f;
			from_b = from_16.z / 16.0f;
			to_a = to_16.y / 16.0f;
			to_b = to_16.z / 16.0f;
			break;
		default:
		}

		from_a = 1.0f - from_a;
		from_b = 1.0f - from_b;
		to_a = 1.0f - to_a;
		to_b = 1.0f - to_b;

		final float[] afloat = new float[] { // :P
				16.0f * (quadsUV[0] + quadsUV[2] * from_a + quadsUV[4] * from_b), // 0
				16.0f * (quadsUV[1] + quadsUV[3] * from_a + quadsUV[5] * from_b), // 1

				16.0f * (quadsUV[0] + quadsUV[2] * to_a + quadsUV[4] * from_b), // 2
				16.0f * (quadsUV[1] + quadsUV[3] * to_a + quadsUV[5] * from_b), // 3

				16.0f * (quadsUV[0] + quadsUV[2] * to_a + quadsUV[4] * to_b), // 2
				16.0f * (quadsUV[1] + quadsUV[3] * to_a + quadsUV[5] * to_b), // 3

				16.0f * (quadsUV[0] + quadsUV[2] * from_a + quadsUV[4] * to_b), // 0
				16.0f * (quadsUV[1] + quadsUV[3] * from_a + quadsUV[5] * to_b), // 1
		};

		return afloat;
	}

	@Override
	public void renderFaceXNeg(BlockPos pos, TextureAtlasSprite sprite) {
		boolean isEdge = renderMinX < 0.0001;
		Vector3f to = new Vector3f((float) renderMinX * 16.0f, (float) renderMinY * 16.0f, (float) renderMinZ * 16.0f);
		Vector3f from = new Vector3f((float) renderMinX * 16.0f, (float) renderMaxY * 16.0f,
				(float) renderMaxZ * 16.0f);

		final EnumFacing myFace = EnumFacing.WEST;
		addFace(myFace, isEdge, to, from, defUVs, sprite);
	}

	@Override
	public void renderFaceYNeg(BlockPos pos, TextureAtlasSprite sprite) {
		boolean isEdge = renderMinY < 0.0001;
		Vector3f to = new Vector3f((float) renderMinX * 16.0f, (float) renderMinY * 16.0f, (float) renderMinZ * 16.0f);
		Vector3f from = new Vector3f((float) renderMaxX * 16.0f, (float) renderMinY * 16.0f,
				(float) renderMaxZ * 16.0f);

		final EnumFacing myFace = EnumFacing.DOWN;
		addFace(myFace, isEdge, to, from, defUVs, sprite);
	}

	@Override
	public void renderFaceZNeg(BlockPos pos, TextureAtlasSprite sprite) {
		boolean isEdge = renderMinZ < 0.0001;
		Vector3f to = new Vector3f((float) renderMinX * 16.0f, (float) renderMinY * 16.0f, (float) renderMinZ * 16.0f);
		Vector3f from = new Vector3f((float) renderMaxX * 16.0f, (float) renderMaxY * 16.0f,
				(float) renderMinZ * 16.0f);

		final EnumFacing myFace = EnumFacing.NORTH;
		addFace(myFace, isEdge, to, from, defUVs, sprite);
	}

	@Override
	public void renderFaceYPos(BlockPos pos, TextureAtlasSprite sprite) {
		boolean isEdge = renderMaxY > 0.9999;
		Vector3f to = new Vector3f((float) renderMinX * 16.0f, (float) renderMaxY * 16.0f, (float) renderMinZ * 16.0f);
		Vector3f from = new Vector3f((float) renderMaxX * 16.0f, (float) renderMaxY * 16.0f,
				(float) renderMaxZ * 16.0f);

		final EnumFacing myFace = EnumFacing.UP;
		addFace(myFace, isEdge, to, from, defUVs, sprite);
	}

	@Override
	public void renderFaceZPos(BlockPos pos, TextureAtlasSprite sprite) {
		boolean isEdge = renderMaxZ > 0.9999;
		Vector3f to = new Vector3f((float) renderMinX * 16.0f, (float) renderMinY * 16.0f, (float) renderMaxZ * 16.0f);
		Vector3f from = new Vector3f((float) renderMaxX * 16.0f, (float) renderMaxY * 16.0f,
				(float) renderMaxZ * 16.0f);

		final EnumFacing myFace = EnumFacing.SOUTH;
		addFace(myFace, isEdge, to, from, defUVs, sprite);
	}

	@Override
	public void renderFaceXPos(BlockPos pos, TextureAtlasSprite sprite) {
		boolean isEdge = renderMaxX > 0.9999;
		Vector3f to = new Vector3f((float) renderMaxX * 16.0f, (float) renderMinY * 16.0f, (float) renderMinZ * 16.0f);
		Vector3f from = new Vector3f((float) renderMaxX * 16.0f, (float) renderMaxY * 16.0f,
				(float) renderMaxZ * 16.0f);

		final EnumFacing myFace = EnumFacing.EAST;
		addFace(myFace, isEdge, to, from, defUVs, sprite);
	}

	protected void addFace(EnumFacing face, boolean isEdge, Vector3f to, Vector3f from, float[] defUVs2, TextureAtlasSprite texture) {
		faces.add(new ModelFace(face, isEdge, color, to, from, defUVs2, texture));
	}

	@Override
	public IBakedModel bakeModel(boolean flip) {
		ModelRotation mr = ModelRotation.X0_Y0;

		if (flip)
			mr = ModelRotation.X0_Y180;

		for (ModelFace face : faces) {
			final EnumFacing myFace = face.face;
			final float[] uvs = getFaceUvs(myFace, face.from, face.to);

			final BlockFaceUV uv = new BlockFaceUV(uvs, 0);
			final BlockPartFace bpf = new BlockPartFace(myFace, face.color, "", uv);

			BakedQuad bf = faceBakery.makeBakedQuad(face.to, face.from, bpf, face.spite, myFace, mr, null, true, true);
			bf = new IColoredBakedQuad.ColoredBakedQuad(bf.getVertexData(), face.color, bf.getFace());

			if (face.isEdge)
				this.generatedModel.getFaceQuads(myFace).add(bf);
			else
				this.generatedModel.getGeneralQuads().add(bf);
		}
		return clearBaker();
	}

	protected CachedModel clearBaker() {
		CachedModel model = generatedModel.copy();
		generatedModel = new CachedModel();
		faces = new ArrayList<ModelFace>();
		return model;
	}

	@Override
	public double getRenderMaxX() {
		return renderMaxX;
	}

	@Override
	public double getRenderMaxY() {
		return renderMaxY;
	}

	@Override
	public double getRenderMaxZ() {
		return renderMaxZ;
	}
	
	public void setRenderMaxX(double renderMaxX) {
		this.renderMaxX = renderMaxX;
	}
	
	public void setRenderMaxY(double renderMaxY) {
		this.renderMaxY = renderMaxY;
	}
	
	public void setRenderMaxZ(double renderMaxZ) {
		this.renderMaxZ = renderMaxZ;
	}
	
	@Override
	public double getRenderMinX() {
		return renderMinX;
	}

	@Override
	public double getRenderMinY() {
		return renderMinY;
	}

	@Override
	public double getRenderMinZ() {
		return renderMinZ;
	}
	
	public void setRenderMinX(double renderMinX) {
		this.renderMinX = renderMinX;
	}
	
	public void setRenderMinY(double renderMinY) {
		this.renderMinY = renderMinY;
	}
	
	public void setRenderMinZ(double renderMinZ) {
		this.renderMinZ = renderMinZ;
	}

}
