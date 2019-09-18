package forestry.storage.features;

import net.minecraft.item.ItemStack;

import forestry.core.config.Constants;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.storage.items.ItemCrated;

public class CreateItems {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.CRATE);

	//TODO map of item to crate or similar?
	public static final FeatureItem<ItemCrated> CRATE = REGISTRY.item(() -> new ItemCrated(ItemStack.EMPTY, null), "crate");

	private CreateItems() {
	}
}
