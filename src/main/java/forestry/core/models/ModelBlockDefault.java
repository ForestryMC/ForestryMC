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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import forestry.core.models.baker.ModelBaker;
import forestry.core.models.baker.ModelBakerModel;
import forestry.core.utils.ResourceUtil;

@OnlyIn(Dist.CLIENT)
public abstract class ModelBlockDefault<B extends Block, K> implements IBakedModel {
    @Nullable
    private ItemOverrideList overrideList;

    protected final Class<B> blockClass;

    @Nullable
    protected ModelBakerModel blockModel;
    @Nullable
    protected ModelBakerModel itemModel;

    protected ModelBlockDefault(Class<B> blockClass) {
        this.blockClass = blockClass;
    }

    protected IBakedModel bakeModel(BlockState state, K key, B block, IModelData extraData) {
        ModelBaker baker = new ModelBaker();

        bakeBlock(block, extraData, key, baker, false);

        blockModel = baker.bake(false);
        onCreateModel(blockModel);
        return blockModel;
    }

    protected IBakedModel getModel(BlockState state, IModelData extraData) {
        Preconditions.checkArgument(blockClass.isInstance(state.getBlock()));

        K worldKey = getWorldKey(state, extraData);
        B block = blockClass.cast(state.getBlock());
        return bakeModel(state, worldKey, block, extraData);
    }

    protected IBakedModel bakeModel(ItemStack stack, World world, K key) {
        ModelBaker baker = new ModelBaker();
        Block block = Block.getBlockFromItem(stack.getItem());
        Preconditions.checkArgument(blockClass.isInstance(block));
        B bBlock = blockClass.cast(block);
        bakeBlock(bBlock, EmptyModelData.INSTANCE, key, baker, true);

        return itemModel = baker.bake(true);
    }

    protected IBakedModel getModel(ItemStack stack, World world) {
        return bakeModel(stack, world, getInventoryKey(stack));
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        Preconditions.checkNotNull(state);
        IBakedModel model = getModel(state, extraData);
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
    public boolean func_230044_c_() {
        return itemModel != null && itemModel.func_230044_c_();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        if (blockModel != null) {
            return blockModel.getParticleTexture();
        }
        return ResourceUtil.getMissingTexture();
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

    protected abstract K getWorldKey(BlockState state, IModelData extraData);

    protected abstract void bakeBlock(B block, IModelData extraData, K key, ModelBaker baker, boolean inventory);

    private class DefaultItemOverrideList extends ItemOverrideList {
        public DefaultItemOverrideList() {
            super();
        }

        @Nullable
        @Override
        public IBakedModel func_239290_a_(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
            if (world == null) {
                world = Minecraft.getInstance().world;
            }
            return getModel(stack, world);
        }
    }
}
