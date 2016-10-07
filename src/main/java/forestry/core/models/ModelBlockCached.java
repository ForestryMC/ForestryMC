package forestry.core.models;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class ModelBlockCached<B extends Block, K extends Object> extends ModelBlockDefault<B, K> {
    private static final boolean DISABLE_CACHE = false;
    private static final Set<ModelBlockCached> CACHE_PROVIDERS = new HashSet<>();
    private final Cache<K, IBakedModel> inventoryCache, worldCache;

    public static void clear() {
        for (ModelBlockCached modelBlockCached : CACHE_PROVIDERS) {
            modelBlockCached.worldCache.invalidateAll();
            modelBlockCached.inventoryCache.invalidateAll();
        }
    }

    protected ModelBlockCached(@Nonnull Class<B> blockClass) {
        super(blockClass);

        worldCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
        inventoryCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
    }

    @Override
    protected IBakedModel getModel(IBlockState state, IBlockAccess world, BlockPos pos) {
        K key = getWorldKey(state, world, pos);
        if (key == null) {
            return null;
        }

        if (DISABLE_CACHE) {
            return bakeModel(state, key);
        }

        IBakedModel model = worldCache.getIfPresent(key);
        if (model == null) {
            model = bakeModel(state, key);
            worldCache.put(key, model);
        }
        return model;
    }

    @Override
    protected IBakedModel getModel(ItemStack stack, World world) {
        K key = getInventoryKey(stack);
        if (key == null) {
            return null;
        }

        if (DISABLE_CACHE) {
            return bakeModel(stack, world, key);
        }

        IBakedModel model = inventoryCache.getIfPresent(key);
        if (model == null) {
            model = bakeModel(stack, world, key);
            inventoryCache.put(key, model);
        }
        return model;
    }
}
