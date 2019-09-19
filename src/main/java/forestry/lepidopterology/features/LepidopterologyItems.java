package forestry.lepidopterology.features;

import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.core.config.Constants;
import forestry.lepidopterology.items.ItemButterflyGE;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

public class LepidopterologyItems {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.LEPIDOPTEROLOGY);

	//public static final FeatureItemGroup<ItemButterflyGE, EnumFlutterType> GENTICS = REGISTRY.itemGroup(ItemButterflyGE::new, "_ge")
	//TODO: Clean up feature groups so we can use affix at items too
	public static final FeatureItem<ItemButterflyGE> BUTTERFLY_GE = REGISTRY.item(() -> new ItemButterflyGE(EnumFlutterType.BUTTERFLY), "butterfly_ge");
	public static final FeatureItem<ItemButterflyGE> SERUM_GE = REGISTRY.item(() -> new ItemButterflyGE(EnumFlutterType.SERUM), "serum_ge");
	public static final FeatureItem<ItemButterflyGE> CATERPILLAR_GE = REGISTRY.item(() -> new ItemButterflyGE(EnumFlutterType.CATERPILLAR), "caterpillar_ge");
	public static final FeatureItem<ItemButterflyGE> COCOON_GE = REGISTRY.item(() -> new ItemButterflyGE(EnumFlutterType.COCOON), "cocoon_ge");

	private LepidopterologyItems() {
	}
}
