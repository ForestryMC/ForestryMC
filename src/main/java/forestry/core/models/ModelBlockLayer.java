package forestry.core.models;

import forestry.core.models.baker.ModelBaker;
import forestry.core.utils.ResourceUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nullable;

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
    protected K getWorldKey(BlockState state, IModelData extraData) {
        return provider.getWorldKey(state, extraData);
    }

    @Override
    protected void bakeBlock(Block block, IModelData extraData, K key, ModelBaker baker, boolean inventory) {
        for (int layer = 0; layer < provider.getLayerCount(); layer++) {
            TextureAtlasSprite[] textures = new TextureAtlasSprite[6];
            for (Direction direction : Direction.VALUES) {
                TextureAtlasSprite texture = provider.getSprite(key, direction, layer);
                textures[direction.getIndex()] = texture != null ? texture : ResourceUtil.getMissingTexture();
            }
            baker.addBlockModel(textures, provider.getColorIndex(key, layer));
        }

        // Set the particle sprite
        baker.setParticleSprite(provider.getParticle(key));
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    public interface ModelProvider<K> {

        K getInventoryKey(ItemStack stack);

        K getWorldKey(BlockState state, IModelData extraData);

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
