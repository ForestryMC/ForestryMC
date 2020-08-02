package forestry.climatology.features;

import forestry.climatology.ModuleClimatology;
import forestry.climatology.items.ItemHabitatScreen;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class ClimatologyItems {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleClimatology.class);

    public static final FeatureItem<ItemHabitatScreen> HABITAT_SCREEN = REGISTRY.item(ItemHabitatScreen::new, "habitat_screen");

    private ClimatologyItems() {
    }
}
