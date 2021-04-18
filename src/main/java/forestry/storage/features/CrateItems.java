package forestry.storage.features;

import forestry.modules.features.FeatureItem;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.storage.ModuleCrates;
import forestry.storage.items.ItemCrated;

@FeatureProvider
public class CrateItems {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleCrates.class);

	//TODO map of item to crate or similar?
	public static final FeatureItem<ItemCrated> CRATE = REGISTRY.item(ItemCrated::new, "crate");

	private CrateItems() {
	}
}
