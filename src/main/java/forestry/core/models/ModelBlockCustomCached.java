package forestry.core.models;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ModelBlockCustomCached<B extends Block, K> extends ModelBlockCustom<B, K> {
	private static final Set<ModelBlockCustomCached> CACHE_PROVIDERS = new HashSet<>();

	private final Cache<K, IBakedModel> inventoryCache;
	private final Cache<K, IBakedModel> worldCache;

	public static void clear() {
		for (ModelBlockCustomCached modelBlockCached : CACHE_PROVIDERS) {
			modelBlockCached.worldCache.invalidateAll();
			modelBlockCached.inventoryCache.invalidateAll();
			modelBlockCached.onClearCaches();
		}
	}

	protected void onClearCaches() {

	}

	protected ModelBlockCustomCached(Class<B> blockClass) {
		super(blockClass);

		worldCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
		inventoryCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();

		CACHE_PROVIDERS.add(this);
	}

	@Override
	protected IBakedModel getModel(IBlockState state) {
		K key = getWorldKey(state);

		IBakedModel model = worldCache.getIfPresent(key);
		if (model == null) {
			model = super.getModel(state);
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
