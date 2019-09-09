package forestry.core.models;

import com.google.common.base.Preconditions;

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
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//import net.minecraftforge.common.property.IExtendedBlockState;

@OnlyIn(Dist.CLIENT)
public abstract class ModelBlockCustom<B extends Block, K> implements IBakedModel {
	@Nullable
	private ItemOverrideList overrideList;

	protected final Class<B> blockClass;

	@Nullable
	protected IBakedModel blockModel;
	@Nullable
	protected IBakedModel itemModel;

	protected ModelBlockCustom(Class<B> blockClass) {
		this.blockClass = blockClass;
	}

	protected IBakedModel bakeModel(BlockState state, K key, B block) {
		if (false) {//TODO extended statesstate instanceof IExtendedBlockState) {
			//			IExtendedBlockState stateExtended = (IExtendedBlockState) state;
			//			IBlockReader world = stateExtended.getComb(UnlistedBlockAccess.BLOCKACCESS);
			//			BlockPos pos = stateExtended.getComb(UnlistedBlockPos.POS);
		}
		return blockModel = bakeBlock(block, key, false);
	}

	protected IBakedModel getModel(BlockState state) {
		Preconditions.checkArgument(blockClass.isInstance(state.getBlock()));

		K worldKey = getWorldKey(state);
		B block = blockClass.cast(state.getBlock());
		return bakeModel(state, worldKey, block);
	}

	protected IBakedModel bakeModel(ItemStack stack, World world, K key) {
		Block block = Block.getBlockFromItem(stack.getItem());
		Preconditions.checkArgument(blockClass.isInstance(block));
		B bBlock = blockClass.cast(block);

		return itemModel = bakeBlock(bBlock, key, true);
	}

	protected IBakedModel getModel(ItemStack stack, World world) {
		return bakeModel(stack, world, getInventoryKey(stack));
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
		Preconditions.checkNotNull(state);
		IBakedModel model = getModel(state);
		return model.getQuads(state, side, rand);
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
		return Minecraft.getInstance().getTextureMap().missingImage;
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

	protected abstract K getWorldKey(BlockState state);

	protected abstract IBakedModel bakeBlock(B block, K key, boolean inventory);

	private class DefaultItemOverrideList extends ItemOverrideList {
		public DefaultItemOverrideList() {
			super();
		}

		@Override
		public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
			if (world == null) {
				world = Minecraft.getInstance().world;
			}
			return getModel(stack, world);
		}
	}
}
