package forestry.food.features;

import forestry.core.config.Constants;
import forestry.core.items.ItemForestryFood;
import forestry.food.items.ItemAmbrosia;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

public class FoodItems {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.FOOD);

	public static final FeatureItem<ItemForestryFood> HONEYED_SLICE = REGISTRY.item(() -> new ItemForestryFood(8, 0.6f), "honeyed_slice");
	public static final FeatureItem<ItemForestryFood> AMBROSIA = REGISTRY.item(() -> new ItemAmbrosia().setIsDrink(), "ambrosia");
	public static final FeatureItem<ItemForestryFood> HONEY_POT = REGISTRY.item(() -> new ItemForestryFood(2, 0.2f).setIsDrink(), "honey_pot");

	private FoodItems() {
	}
}
