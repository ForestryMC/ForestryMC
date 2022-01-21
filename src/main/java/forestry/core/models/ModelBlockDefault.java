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
package forestry.core.models;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import forestry.core.models.baker.ModelBaker;
import forestry.core.models.baker.ModelBakerModel;
import forestry.core.utils.ResourceUtil;

@OnlyIn(Dist.CLIENT)
public abstract class ModelBlockDefault<B extends Block, K> implements BakedModel {
	@Nullable
	private ItemOverrides overrideList;

	protected final Class<B> blockClass;

	@Nullable
	protected ModelBakerModel blockModel;
	@Nullable
	protected ModelBakerModel itemModel;

	protected ModelBlockDefault(Class<B> blockClass) {
		this.blockClass = blockClass;
	}

	protected BakedModel bakeModel(BlockState state, K key, B block, IModelData extraData) {
		ModelBaker baker = new ModelBaker();

		bakeBlock(block, extraData, key, baker, false);

		blockModel = baker.bake(false);
		onCreateModel(blockModel);
		return blockModel;
	}

	protected BakedModel getModel(BlockState state, IModelData extraData) {
		Preconditions.checkArgument(blockClass.isInstance(state.getBlock()));

		K worldKey = getWorldKey(state, extraData);
		B block = blockClass.cast(state.getBlock());
		return bakeModel(state, worldKey, block, extraData);
	}

	protected BakedModel bakeModel(ItemStack stack, Level world, K key) {
		ModelBaker baker = new ModelBaker();
		Block block = Block.byItem(stack.getItem());
		Preconditions.checkArgument(blockClass.isInstance(block));
		B bBlock = blockClass.cast(block);
		bakeBlock(bBlock, EmptyModelData.INSTANCE, key, baker, true);

		return itemModel = baker.bake(true);
	}

	protected BakedModel getModel(ItemStack stack, Level world) {
		return bakeModel(stack, world, getInventoryKey(stack));
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
		Preconditions.checkNotNull(state);
		BakedModel model = getModel(state, extraData);
		return model.getQuads(state, side, rand, extraData);
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
		return getQuads(state, side, rand, EmptyModelData.INSTANCE);
	}

	protected void onCreateModel(ModelBakerModel model) {
		model.setAmbientOcclusion(true);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return (itemModel != null || blockModel != null) &&
				(blockModel != null ? blockModel.useAmbientOcclusion() : itemModel.useAmbientOcclusion());
	}

	@Override
	public boolean isGui3d() {
		return itemModel != null && itemModel.isGui3d();
	}

	@Override
	public boolean isCustomRenderer() {
		return (itemModel != null || blockModel != null) &&
				(blockModel != null ? blockModel.isCustomRenderer() : itemModel.isCustomRenderer());
	}

	@Override
	public boolean usesBlockLight() {
		return itemModel != null && itemModel.usesBlockLight();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		if (blockModel != null) {
			return blockModel.getParticleIcon();
		}
		return ResourceUtil.getMissingTexture();
	}

	@Override
	public ItemTransforms getTransforms() {
		if (itemModel == null) {
			return ItemTransforms.NO_TRANSFORMS;
		}
		return itemModel.getTransforms();
	}

	protected ItemOverrides createOverrides() {
		return new DefaultItemOverrideList();
	}

	@Override
	public ItemOverrides getOverrides() {
		if (overrideList == null) {
			overrideList = createOverrides();
		}
		return overrideList;
	}

	protected abstract K getInventoryKey(ItemStack stack);

	protected abstract K getWorldKey(BlockState state, IModelData extraData);

	protected abstract void bakeBlock(B block, IModelData extraData, K key, ModelBaker baker, boolean inventory);

	private class DefaultItemOverrideList extends ItemOverrides {
		public DefaultItemOverrideList() {
			super();
		}

		@Nullable
		@Override
		public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity) {
			if (world == null) {
				world = Minecraft.getInstance().level;
			}
			return getModel(stack, world);
		}
	}
}
