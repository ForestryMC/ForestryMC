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
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.common.property.IExtendedBlockState;

import forestry.api.core.IModelBaker;
import forestry.api.core.IModelBakerModel;
import forestry.core.blocks.propertys.UnlistedBlockAccess;
import forestry.core.blocks.propertys.UnlistedBlockPos;
import forestry.core.models.baker.ModelBaker;

/**
 * A overlay block model to make a block with 2 or more texture layers
 */
public abstract class ModelBlockOverlay<B extends Block> implements IFlexibleBakedModel, ISmartItemModel, ISmartBlockModel {
	@Nonnull
	protected final Class<B> blockClass;
	protected IModelBakerModel latestBlockModel;
	protected IModelBakerModel latestItemModel;

	protected ModelBlockOverlay(@Nonnull Class<B> blockClass) {
		this.blockClass = blockClass;
	}

	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing face) {
		return Collections.emptyList();
	}

	@Override
	public List<BakedQuad> getGeneralQuads() {
		return Collections.emptyList();
	}

	@Override
	public boolean isAmbientOcclusion() {
		if(latestItemModel == null && latestBlockModel == null)
			return false;
		return latestBlockModel != null ? latestBlockModel.isAmbientOcclusion() : latestItemModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return latestItemModel.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		if(latestItemModel == null && latestBlockModel == null)
			return false;
		return latestBlockModel != null ? latestBlockModel.isBuiltInRenderer() : latestItemModel.isBuiltInRenderer();
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture() {
		if(latestBlockModel != null)
			return latestBlockModel.getParticleTexture();
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		if(latestItemModel == null)
			return null;
		return latestItemModel.getItemCameraTransforms();
	}
	
	@Override
	public VertexFormat getFormat() {
		if(latestItemModel == null && latestBlockModel == null)
			return DefaultVertexFormats.BLOCK;
		return latestBlockModel != null ? latestBlockModel.getFormat() : latestItemModel.getFormat();
	}

	@Override
	public IBakedModel handleBlockState(IBlockState state) {
		IModelBaker baker = new ModelBaker();
		IExtendedBlockState stateExtended = (IExtendedBlockState) state;
		
		IBlockAccess world = stateExtended.getValue(UnlistedBlockAccess.BLOCKACCESS);
		BlockPos pos = stateExtended.getValue(UnlistedBlockPos.POS);
		Block block = state.getBlock();
		if (!blockClass.isInstance(block)) {
			return null;
		}
		B bBlock = blockClass.cast(block);
		
		baker.setRenderBoundsFromBlock(block);
		bakeWorldBlock(bBlock, world, pos, baker);
		
		return latestBlockModel = baker.bakeModel(false);
	}

	@Override
	public IBakedModel handleItemState(ItemStack stack) {
		IModelBaker baker = new ModelBaker();
		Block block = Block.getBlockFromItem(stack.getItem());
		if (!blockClass.isInstance(block)) {
			return null;
		}
		B bBlock = blockClass.cast(block);
		
		block.setBlockBoundsForItemRender();
		baker.setRenderBoundsFromBlock(block);
		bakeInventoryBlock(bBlock, stack, baker);
		
		return latestItemModel = baker.bakeModel(true);
	}
	
	protected abstract void bakeInventoryBlock(B block, ItemStack item, IModelBaker baker);

	protected abstract boolean bakeWorldBlock(B block, IBlockAccess world, BlockPos pos, IModelBaker baker);

}
