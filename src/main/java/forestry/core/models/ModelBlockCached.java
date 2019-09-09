package forestry.core.models;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;

@OnlyIn(Dist.CLIENT)
public abstract class ModelBlockCached<B extends Block, K> extends ModelBlockDefault<B, K> {
	private static final Set<ModelBlockCached> CACHE_PROVIDERS = new HashSet<>();

	private final Cache<K, IBakedModel> inventoryCache;
	private final Cache<K, IBakedModel> worldCache;

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
	protected IBakedModel getModel(BlockState state, IModelData extraData) {
		K key = getWorldKey(state, extraData);

		IBakedModel model = worldCache.getIfPresent(key);
		if (model == null) {
			model = super.getModel(state, extraData);
			worldCache.put(key, model);
		}
		return model;
	}

	@Override
	protected IBakedModel getModel(ItemStack stack, World world) {
		K key = getInventoryKey(stack);

		IBakedModel model = inventoryCache.getIfPresent(key);
		if (model == null) {
			model = bakeModel(stack, world, key);
			inventoryCache.put(key, model);
		}
		return model;
	}
}
