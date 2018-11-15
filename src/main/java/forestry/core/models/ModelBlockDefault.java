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

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IModelBaker;
import forestry.api.core.IModelBakerModel;
import forestry.core.blocks.properties.UnlistedBlockAccess;
import forestry.core.blocks.properties.UnlistedBlockPos;
import forestry.core.models.baker.ModelBaker;

@SideOnly(Side.CLIENT)
public abstract class ModelBlockDefault<B extends Block, K> implements IBakedModel {
	@Nullable
	private ItemOverrideList overrideList;

	protected final Class<B> blockClass;

	@Nullable
	protected IModelBakerModel blockModel;
	@Nullable
	protected IModelBakerModel itemModel;

	protected ModelBlockDefault(Class<B> blockClass) {
		this.blockClass = blockClass;
	}

	protected IBakedModel bakeModel(IBlockState state, K key, B block) {
		IModelBaker baker = new ModelBaker();

		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState stateExtended = (IExtendedBlockState) state;
			IBlockAccess world = stateExtended.getValue(UnlistedBlockAccess.BLOCKACCESS);
			BlockPos pos = stateExtended.getValue(UnlistedBlockPos.POS);
		}

		bakeBlock(block, key, baker, false);

		blockModel = baker.bakeModel(false);
		onCreateModel(blockModel);
		return blockModel;
	}

	protected IBakedModel getModel(IBlockState state) {
		Preconditions.checkArgument(blockClass.isInstance(state.getBlock()));

		K worldKey = getWorldKey(state);
		B block = blockClass.cast(state.getBlock());
		return bakeModel(state, worldKey, block);
	}

	protected IBakedModel bakeModel(ItemStack stack, World world, K key) {
		IModelBaker baker = new ModelBaker();
		Block block = Block.getBlockFromItem(stack.getItem());
		Preconditions.checkArgument(blockClass.isInstance(block));
		B bBlock = blockClass.cast(block);
		bakeBlock(bBlock, key, baker, true);

		return itemModel = baker.bakeModel(true);
	}

	protected IBakedModel getModel(ItemStack stack, World world) {
		return bakeModel(stack, world, getInventoryKey(stack));
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
		Preconditions.checkNotNull(state);
		IBakedModel model = getModel(state);
		return model.getQuads(state, side, rand);
	}

	protected void onCreateModel(IModelBakerModel model) {
		model.setAmbientOcclusion(true);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return (itemModel != null || blockModel != null) &&
			(blockModel != null ? blockModel.isAmbientOcclusion() : itemModel.isAmbientOcclusion());
	}

	@Override
	public boolean isGui3d() {
		return itemModel != null && itemModel.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return (itemModel != null || blockModel != null) &&
			(blockModel != null ? blockModel.isBuiltInRenderer() : itemModel.isBuiltInRenderer());
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		if (blockModel != null) {
			return blockModel.getParticleTexture();
		}
		return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		if (itemModel == null) {
			return ItemCameraTransforms.DEFAULT;
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

	protected abstract K getInventoryKey(ItemStack stack);

	protected abstract K getWorldKey(IBlockState state);

	protected abstract void bakeBlock(B block, K key, IModelBaker baker, boolean inventory);

	private class DefaultItemOverrideList extends ItemOverrideList {
		public DefaultItemOverrideList() {
			super(Collections.emptyList());
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
			if (world == null) {
				world = Minecraft.getMinecraft().world;
			}
			return getModel(stack, world);
		}
	}
}
