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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import net.minecraftforge.client.model.IColoredBakedQuad;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.util.vector.Vector3f;

import forestry.api.core.ForestryAPI;
import forestry.api.core.IModelBaker;
import forestry.api.core.IModelBakerModel;
import forestry.core.proxy.Proxies;

/**
 * A model baker to make custom models in the mod
 */
@SideOnly(Side.CLIENT)
public class ModelBaker implements IModelBaker {
	
	private static final ModelBaker instance = new ModelBaker();

	static {
		ForestryAPI.modelBaker = instance;
	}

	public static ModelBaker getInstance() {
		return instance;
	}

	protected double renderMinX;
	protected double renderMaxX;

	protected double renderMinY;
	protected double renderMaxY;

	protected double renderMinZ;
	protected double renderMaxZ;

	protected ModelBakerModel currentModel = new ModelBakerModel();

	protected final FaceBakery faceBakery = new FaceBakery();

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

	protected int colorIndex = -1;

	@Override
	public void setColorIndex(int colorIndex) {
		this.colorIndex = colorIndex;
	}
	
	@Override
	public void addBlockModel(Block block, BlockPos pos, TextureAtlasSprite[] textures, int colorIndex) {
		setRenderBoundsFromBlock(block);
		
		setColorIndex(colorIndex);
		
		World world = Proxies.common.getRenderWorld();
		BlockPos posDOWN = null;
		BlockPos posUP = null;
		BlockPos posEAST = null;
		BlockPos posWEST = null;
		BlockPos posNORTH = null;
		BlockPos posSOUTH = null;
		if(pos != null){
			posDOWN = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
			posUP = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
			posEAST = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
			posWEST = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
			posNORTH = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
			posSOUTH = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
		}

		if (pos == null || block.shouldSideBeRendered(world, posDOWN, EnumFacing.DOWN)) {
			addFaceYNeg(textures[EnumFacing.DOWN.ordinal()]);
		}

		if (pos == null || block.shouldSideBeRendered(world, posUP, EnumFacing.UP)) {
			addFaceYPos(textures[EnumFacing.UP.ordinal()]);
		}

		if (pos == null || block.shouldSideBeRendered(world, posEAST, EnumFacing.EAST)) {
			addFaceXNeg(textures[EnumFacing.EAST.ordinal()]);
		}

		if (pos == null || block.shouldSideBeRendered(world, posWEST, EnumFacing.WEST)) {
			addFaceXPos(textures[EnumFacing.WEST.ordinal()]);
		}

		if (pos == null || block.shouldSideBeRendered(world, posNORTH, EnumFacing.NORTH)) {
			addFaceZNeg(textures[EnumFacing.NORTH.ordinal()]);
		}

		if (pos == null || block.shouldSideBeRendered(world, posSOUTH, EnumFacing.SOUTH)) {
			addFaceZPos(textures[EnumFacing.SOUTH.ordinal()]);
		}
		setRenderBounds(0, 0, 0, 1, 1, 1);
	}

	@Override
	public void addBlockModel(Block block, BlockPos pos, TextureAtlasSprite texture, int colorIndex) {
		addBlockModel(block, pos, new TextureAtlasSprite[]{ texture ,  texture, texture, texture, texture, texture }, colorIndex);
	}

	private final float quadsUV[] = new float[] { 0, 0, 1, 1, 0, 0, 1, 1 };
	protected EnumSet<EnumFacing> renderFaces = EnumSet.allOf(EnumFacing.class);
	private List<ModelBakerFace> faces = new ArrayList<>();

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
	public void addFaceXNeg(TextureAtlasSprite sprite) {
		boolean isEdge = renderMinX < 0.0001;
		Vector3f to = new Vector3f((float) renderMinX * 16.0f, (float) renderMinY * 16.0f, (float) renderMinZ * 16.0f);
		Vector3f from = new Vector3f((float) renderMinX * 16.0f, (float) renderMaxY * 16.0f, (float) renderMaxZ * 16.0f);

		addFace(EnumFacing.WEST, isEdge, to, from, defUVs, sprite);
	}

	@Override
	public void addFaceYNeg(TextureAtlasSprite sprite) {
		boolean isEdge = renderMinY < 0.0001;
		Vector3f to = new Vector3f((float) renderMinX * 16.0f, (float) renderMinY * 16.0f, (float) renderMinZ * 16.0f);
		Vector3f from = new Vector3f((float) renderMaxX * 16.0f, (float) renderMinY * 16.0f, (float) renderMaxZ * 16.0f);

		addFace(EnumFacing.DOWN, isEdge, to, from, defUVs, sprite);
	}

	@Override
	public void addFaceZNeg(TextureAtlasSprite sprite) {
		boolean isEdge = renderMinZ < 0.0001;
		Vector3f to = new Vector3f((float) renderMinX * 16.0f, (float) renderMinY * 16.0f, (float) renderMinZ * 16.0f);
		Vector3f from = new Vector3f((float) renderMaxX * 16.0f, (float) renderMaxY * 16.0f, (float) renderMinZ * 16.0f);

		addFace(EnumFacing.NORTH, isEdge, to, from, defUVs, sprite);
	}

	@Override
	public void addFaceYPos(TextureAtlasSprite sprite) {
		boolean isEdge = renderMaxY > 0.9999;
		Vector3f to = new Vector3f((float) renderMinX * 16.0f, (float) renderMaxY * 16.0f, (float) renderMinZ * 16.0f);
		Vector3f from = new Vector3f((float) renderMaxX * 16.0f, (float) renderMaxY * 16.0f, (float) renderMaxZ * 16.0f);

		addFace(EnumFacing.UP, isEdge, to, from, defUVs, sprite);
	}

	@Override
	public void addFaceZPos(TextureAtlasSprite sprite) {
		boolean isEdge = renderMaxZ > 0.9999;
		Vector3f to = new Vector3f((float) renderMinX * 16.0f, (float) renderMinY * 16.0f, (float) renderMaxZ * 16.0f);
		Vector3f from = new Vector3f((float) renderMaxX * 16.0f, (float) renderMaxY * 16.0f, (float) renderMaxZ * 16.0f);

		addFace(EnumFacing.SOUTH, isEdge, to, from, defUVs, sprite);
	}

	@Override
	public void addFaceXPos(TextureAtlasSprite sprite) {
		
		boolean isEdge = renderMaxX > 0.9999;
		Vector3f to = new Vector3f((float) renderMaxX * 16.0f, (float) renderMinY * 16.0f, (float) renderMinZ * 16.0f);
		Vector3f from = new Vector3f((float) renderMaxX * 16.0f, (float) renderMaxY * 16.0f, (float) renderMaxZ * 16.0f);

		addFace(EnumFacing.EAST, isEdge, to, from, defUVs, sprite);
	}

	protected void addFace(@Nonnull EnumFacing face, boolean isEdge, Vector3f to, Vector3f from, float[] defUVs2, TextureAtlasSprite texture) {
		faces.add(new ModelBakerFace(face, isEdge, colorIndex, to, from, defUVs2, texture));
	}

	@Override
	public IModelBakerModel bakeModel(boolean flip) {
		ModelRotation mr = ModelRotation.X0_Y0;

		if (flip)
			mr = ModelRotation.X0_Y180;

		// TODO: find out why there is a concurrent modification issue with faces
		for (ModelBakerFace face : new ArrayList<>(faces)) {
			final EnumFacing myFace = face.face;
			final float[] uvs = getFaceUvs(myFace, face.from, face.to);

			final BlockFaceUV uv = new BlockFaceUV(uvs, 0);
			final BlockPartFace bpf = new BlockPartFace(myFace, face.colorIndex, "", uv);

			BakedQuad bf = faceBakery.makeBakedQuad(face.to, face.from, bpf, face.spite, myFace, mr, null, true, true);
			bf = new IColoredBakedQuad.ColoredBakedQuad(bf.getVertexData(), face.colorIndex, bf.getFace());

			if (face.isEdge)
				this.currentModel.getFaceQuads(myFace).add(bf);
			else
				this.currentModel.getGeneralQuads().add(bf);
		}
		return clear();
	}

	@Override
	public IModelBakerModel clear() {
		ModelBakerModel model = currentModel.copy();
		currentModel = new ModelBakerModel();
		faces = new ArrayList<>();
		return model;
	}
	
	@Override
	public IModelBakerModel getCurrentModel() {
		return currentModel;
	}

}
