package forestry.food.features;

import forestry.core.items.ItemForestryFood;
import forestry.food.ModuleFood;
import forestry.food.items.ItemAmbrosia;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class FoodItems {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleFood.class);

    public static final FeatureItem<ItemForestryFood> HONEYED_SLICE = REGISTRY.item(() -> new ItemForestryFood(8, 0.6f), "honeyed_slice");
    public static final FeatureItem<ItemForestryFood> AMBROSIA = REGISTRY.item(() -> new ItemAmbrosia().setIsDrink(), "ambrosia");
    public static final FeatureItem<ItemForestryFood> HONEY_POT = REGISTRY.item(() -> new ItemForestryFood(2, 0.2f).setIsDrink(), "honey_pot");

    private FoodItems() {
    }
}
