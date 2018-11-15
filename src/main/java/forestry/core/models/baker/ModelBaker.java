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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.model.IModelState;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IModelBaker;
import forestry.api.core.IModelBakerModel;
import forestry.core.models.ModelManager;

import org.lwjgl.util.vector.Vector3f;

/**
 * A model baker to make custom block models with more than one texture layer.
 */
@SideOnly(Side.CLIENT)
public final class ModelBaker implements IModelBaker {

	private static final float[] UVS = new float[]{0.0F, 0.0F, 16.0F, 16.0F, 0.0F, 0.0F, 16.0F, 16.0F};
	private static final FaceBakery FACE_BAKERY = new FaceBakery();
	private static final Vector3f POS_FROM = new Vector3f(0.0F, 0.0F, 0.0F);
	private static final Vector3f POS_TO = new Vector3f(16.0F, 16.0F, 16.0F);

	private final List<ModelBakerFace> faces = new ArrayList<>();
	private final List<Pair<IBlockState, IBakedModel>> bakedModels = new ArrayList<>();
	private final List<Pair<IBlockState, IBakedModel>> bakedModelsPost = new ArrayList<>();

	protected final ModelBakerModel currentModel = new ModelBakerModel(ModelManager.getInstance().getDefaultBlockState());

	protected int colorIndex = -1;

	@Override
	public void setColorIndex(int colorIndex) {
		this.colorIndex = colorIndex;
	}

	@Override
	public void addModel(TextureAtlasSprite[] textures, int colorIndex) {
		setColorIndex(colorIndex);

		for (EnumFacing facing : EnumFacing.VALUES) {
			addFace(facing, textures[facing.ordinal()]);
		}
	}

	@Override
	public void addModel(TextureAtlasSprite texture, int colorIndex) {
		addModel(new TextureAtlasSprite[]{texture, texture, texture, texture, texture, texture}, colorIndex);
	}

	@Override
	public void addBlockModel(@Nullable BlockPos pos, TextureAtlasSprite[] textures, int colorIndex) {
		setColorIndex(colorIndex);

		if (pos != null) {
			World world = Minecraft.getMinecraft().world;
			IBlockState state = world.getBlockState(pos);
			for (EnumFacing facing : EnumFacing.VALUES) {
				if (state.shouldSideBeRendered(world, pos, facing)) {
					addFace(facing, textures[facing.ordinal()]);
				}
			}
		} else {
			for (EnumFacing facing : EnumFacing.VALUES) {
				addFace(facing, textures[facing.ordinal()]);
			}
		}

	}

	@Override
	public void addBlockModel(@Nullable BlockPos pos, TextureAtlasSprite texture, int colorIndex) {
		addBlockModel(pos, new TextureAtlasSprite[]{texture, texture, texture, texture, texture, texture},
			colorIndex);
	}

	@Override
	public void addBakedModel(@Nullable IBlockState state, IBakedModel model) {
		this.bakedModels.add(Pair.of(state, model));
	}

	@Override
	public void addBakedModelPost(@Nullable IBlockState state, IBakedModel model) {
		this.bakedModelsPost.add(Pair.of(state, model));
	}

	@Override
	public void addFace(EnumFacing facing, TextureAtlasSprite sprite) {
		if (sprite != Minecraft.getMinecraft().getTextureMapBlocks().missingImage) {
			faces.add(new ModelBakerFace(facing, colorIndex, sprite));
		}
	}

	@Override
	public IModelBakerModel bakeModel(boolean flip) {
		ModelRotation modelRotation = ModelRotation.X0_Y0;

		if (flip) {
			modelRotation = ModelRotation.X0_Y180;
		}

		// Add baked models to the current model.
		for (Pair<IBlockState, IBakedModel> bakedModel : bakedModels) {
			currentModel.addModelQuads(bakedModel);
		}

		for (Pair<IBlockState, IBakedModel> bakedModel : bakedModelsPost) {
			currentModel.addModelQuadsPost(bakedModel);
		}

		for (ModelBakerFace face : faces) {
			EnumFacing facing = face.face;
			BlockFaceUV uvFace = new BlockFaceUV(UVS, 0);
			BlockPartFace partFace = new BlockPartFace(facing, face.colorIndex, "", uvFace);
			BakedQuad quad = FACE_BAKERY.makeBakedQuad(POS_FROM, POS_TO, partFace, face.spite, facing, modelRotation,
				null, true, true);

			currentModel.addQuad(facing, quad);
		}

		return currentModel;
	}

	@Override
	public void setModelState(@Nullable IModelState modelState) {
		currentModel.setModelState(modelState);
	}

	@Override
	public void setParticleSprite(TextureAtlasSprite particleSprite) {
		currentModel.setParticleSprite(particleSprite);
	}
}
