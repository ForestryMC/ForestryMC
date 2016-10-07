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

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.property.IExtendedBlockState;

import forestry.api.core.IModelBaker;
import forestry.api.core.IModelBakerModel;
import forestry.core.blocks.propertys.UnlistedBlockAccess;
import forestry.core.blocks.propertys.UnlistedBlockPos;
import forestry.core.models.baker.ModelBaker;

public abstract class ModelBlockDefault<B extends Block, K extends Object> implements IBakedModel {
	private ItemOverrideList overrideList;
	@Nonnull
	protected final Class<B> blockClass;
	
	protected IModelBakerModel blockModel;
	protected IModelBakerModel itemModel;

	protected ModelBlockDefault(@Nonnull Class<B> blockClass) {
		this.blockClass = blockClass;
	}

	protected IBakedModel bakeModel(IBlockState state, K key) {
		if (key == null) {
			return null;
		}

		if(state instanceof IExtendedBlockState) {
			IModelBaker baker = new ModelBaker();
			IExtendedBlockState stateExtended = (IExtendedBlockState) state;

			IBlockAccess world = stateExtended.getValue(UnlistedBlockAccess.BLOCKACCESS);
			BlockPos pos = stateExtended.getValue(UnlistedBlockPos.POS);
			Block block = state.getBlock();
			if (!blockClass.isInstance(block)) {
				return null;
			}
			B bBlock = blockClass.cast(block);

			baker.setRenderBounds(state.getBoundingBox(world, pos));
			bakeBlock(bBlock, key, baker, false);

			blockModel = baker.bakeModel(false);
			onCreateModel(blockModel);
			return blockModel;
		} else {
			return null;
		}
	}

	protected IBakedModel getModel(IBlockState state, IBlockAccess world, BlockPos pos) {
		return bakeModel(state, getWorldKey(state, world, pos));
	}

	protected IBakedModel bakeModel(ItemStack stack, World world, K key) {
		if (key == null) {
			return null;
		}

		IModelBaker baker = new ModelBaker();
		Block block = Block.getBlockFromItem(stack.getItem());
		if (!blockClass.isInstance(block)) {
			return null;
		}
		B bBlock = blockClass.cast(block);

		// FIXME: This way of getting the IBlockState will probably backfire.
		IBlockState state = block.getStateFromMeta(stack.getItemDamage());
		baker.setRenderBounds(state.getBoundingBox(world, null));
		bakeBlock(bBlock, key, baker, true);

		return itemModel = baker.bakeModel(true);
	}

	protected IBakedModel getModel(ItemStack stack, World world) {
		return bakeModel(stack, world, getInventoryKey(stack));
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		IBakedModel model;

		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState stateExtended = (IExtendedBlockState) state;
			IBlockAccess world = stateExtended.getValue(UnlistedBlockAccess.BLOCKACCESS);
			BlockPos pos = stateExtended.getValue(UnlistedBlockPos.POS);
			model = getModel(state, world, pos);
		} else {
			model = getModel(state, null, null);
		}

		if (model != null) {
			return model.getQuads(state, side, rand);
		} else {
			return Collections.emptyList();
		}
	}
	
	protected void onCreateModel(IModelBakerModel model){
		model.setAmbientOcclusion(true);
	}

	@Override
	public boolean isAmbientOcclusion() {
		if(itemModel == null && blockModel == null) {
			return false;
		}
		return blockModel != null ? blockModel.isAmbientOcclusion() : itemModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		if(itemModel == null) {
			return false;
		}
		return itemModel.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		if(itemModel == null && blockModel == null) {
			return false;
		}
		return blockModel != null ? blockModel.isBuiltInRenderer() : itemModel.isBuiltInRenderer();
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture() {
		if(blockModel != null) {
			return blockModel.getParticleTexture();
		}
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		if(itemModel == null) {
			return null;
		}
		return itemModel.getItemCameraTransforms();
	}

	protected ItemOverrideList createOverrides() {
		return new DefaultItemOverrideList();
	}

	@Override
	public ItemOverrideList getOverrides() {
		if (overrideList == null) {
			overrideList = createOverrides();
		}
		return overrideList;
	}

	protected abstract K getInventoryKey(@Nonnull ItemStack stack);
	protected abstract K getWorldKey(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos);
	protected abstract void bakeBlock(@Nonnull B block, @Nonnull K key, @Nonnull IModelBaker baker, boolean inventory);

	private class DefaultItemOverrideList extends ItemOverrideList {
		public DefaultItemOverrideList() {
			super(Collections.emptyList());
		}
		
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			return getModel(stack, world);
		}
	}
}
