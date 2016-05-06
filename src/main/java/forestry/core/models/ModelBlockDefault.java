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

/**
 * A overlay block model to make a block with 2 or more texture layers
 */
public abstract class ModelBlockDefault<B extends Block> implements IBakedModel {
	private ItemOverrideList overrideList;
	@Nonnull
	protected final Class<B> blockClass;
	protected IModelBakerModel blockModel;
	protected IModelBakerModel itemModel;

	protected ModelBlockDefault(@Nonnull Class<B> blockClass) {
		this.blockClass = blockClass;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		if(state instanceof IExtendedBlockState){
			IModelBaker baker = new ModelBaker();
			IExtendedBlockState stateExtended = (IExtendedBlockState) state;
	
			IBlockAccess world = stateExtended.getValue(UnlistedBlockAccess.BLOCKACCESS);
			BlockPos pos = stateExtended.getValue(UnlistedBlockPos.POS);
			Block block = state.getBlock();
			if (!blockClass.isInstance(block)) {
				return null;
			}
			B bBlock = blockClass.cast(block);
	
			baker.setRenderBounds(block.getBoundingBox(state, world, pos));
			bakeWorldBlock(bBlock, world, pos, stateExtended, baker);
	
			blockModel = baker.bakeModel(false);
			return blockModel.getQuads(state, side, rand);
		}
		return Collections.emptyList();
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

	protected abstract void bakeInventoryBlock(@Nonnull B block, @Nonnull ItemStack item, @Nonnull IModelBaker baker);

	protected abstract void bakeWorldBlock(@Nonnull B block, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IExtendedBlockState stateExtended, @Nonnull IModelBaker baker);

	private class DefaultItemOverrideList extends ItemOverrideList {
		public DefaultItemOverrideList() {
			super(Collections.emptyList());
		}
		
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			IModelBaker baker = new ModelBaker();
			Block block = Block.getBlockFromItem(stack.getItem());
			if (!blockClass.isInstance(block)) {
				return null;
			}
			B bBlock = blockClass.cast(block);

			baker.setRenderBounds(block.getBoundingBox(block.getStateFromMeta(stack.getItemDamage()), world, null));
			bakeInventoryBlock(bBlock, stack, baker);

			return itemModel = baker.bakeModel(true);
		}
	}
}
