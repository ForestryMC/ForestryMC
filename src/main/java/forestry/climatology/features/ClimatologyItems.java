package forestry.climatology.features;

import forestry.climatology.items.ItemHabitatScreen;
import forestry.core.config.Constants;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

public class ClimatologyItems {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.CLIMATOLOGY);

	public static final FeatureItem<ItemHabitatScreen> HABITAT_SCREEN = REGISTRY.item(ItemHabitatScreen::new, "habitat_screen");

	private ClimatologyItems() {
	}
}
