package forestry.core.models;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ModelItemCached<K> extends ModelItemDefault<K> {
	private static final Set<ModelItemCached> CACHE_PROVIDERS = new HashSet<>();

	private final Cache<K, IBakedModel> cache;

	public static void clear() {
		for (ModelItemCached modelBlockCached : CACHE_PROVIDERS) {
			modelBlockCached.cache.invalidateAll();
		}
	}

	protected ModelItemCached() {
		cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();

		CACHE_PROVIDERS.add(this);
	}

	@Override
	protected IBakedModel getModel(ItemStack stack) {
		K key = getInventoryKey(stack);

		IBakedModel model = cache.getIfPresent(key);
		if (model == null) {
			model = bakeModel(stack, key);
			cache.put(key, model);
		}
		return model;
	}
}
