package forestry.core.models;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;

import net.minecraftforge.client.model.data.ModelData;

import forestry.core.models.baker.ModelBaker;
import forestry.core.utils.ResourceUtil;

public class ModelBlockLayer<K> extends ModelBlockCached<Block, K> {

	private final ModelProvider<K> provider;

	public ModelBlockLayer(ModelProvider<K> provider) {
		super(Block.class);
		this.provider = provider;
	}

	@Override
	protected K getInventoryKey(ItemStack stack) {
		return provider.getInventoryKey(stack);
	}

	@Override
	protected K getWorldKey(BlockState state, ModelData extraData) {
		return provider.getWorldKey(state, extraData);
	}

	@Override
	protected void bakeBlock(Block block, ModelData extraData, K key, ModelBaker baker, boolean inventory) {
		for (int layer = 0; layer < provider.getLayerCount(); layer++) {
			TextureAtlasSprite[] textures = new TextureAtlasSprite[6];
			for (Direction direction : Direction.VALUES) {
				TextureAtlasSprite texture = provider.getSprite(key, direction, layer);
				textures[direction.get3DDataValue()] = texture != null ? texture : ResourceUtil.getMissingTexture();
			}
			baker.addBlockModel(textures, provider.getColorIndex(key, layer));
		}

		// Set the particle sprite
		baker.setParticleSprite(provider.getParticle(key));
	}

	public interface ModelProvider<K> {

		K getInventoryKey(ItemStack stack);

		K getWorldKey(BlockState state, ModelData extraData);

		default int getLayerCount() {
			return 2;
		}

		@Nullable
		TextureAtlasSprite getSprite(K data, Direction direction, int layer);

		default int getColorIndex(K data, int layer) {
			return layer;
		}

		TextureAtlasSprite getParticle(K data);
	}
}
