package forestry.core.models;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
public abstract class ModelBlockCached<B extends Block, K> extends ModelBlockDefault<B, K> {
	private static final Set<ModelBlockCached> CACHE_PROVIDERS = new HashSet<>();

	private final Cache<K, BakedModel> inventoryCache;
	private final Cache<K, BakedModel> worldCache;

	public static void clear() {
		for (ModelBlockCached modelBlockCached : CACHE_PROVIDERS) {
			modelBlockCached.worldCache.invalidateAll();
			modelBlockCached.inventoryCache.invalidateAll();
		}
	}

	protected ModelBlockCached(Class<B> blockClass) {
		super(blockClass);

		worldCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
		inventoryCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();

		CACHE_PROVIDERS.add(this);
	}

	@Override
	protected BakedModel getModel(BlockState state, ModelData extraData) {
		K key = getWorldKey(state, extraData);

		BakedModel model = worldCache.getIfPresent(key);
		if (model == null) {
			model = super.getModel(state, extraData);
			worldCache.put(key, model);
		}
		return model;
	}

	@Override
	protected BakedModel getModel(ItemStack stack, Level world) {
		K key = getInventoryKey(stack);

		BakedModel model = inventoryCache.getIfPresent(key);
		if (model == null) {
			model = bakeModel(stack, world, key);
			inventoryCache.put(key, model);
		}
		return model;
	}
}
