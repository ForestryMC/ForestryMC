package forestry.lepidopterology.features;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;

import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.lepidopterology.ModuleLepidopterology;
import forestry.lepidopterology.items.ItemButterflyGE;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class LepidopterologyItems {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleLepidopterology.class);

	//public static final FeatureItemGroup<ItemButterflyGE, EnumFlutterType> GENTICS = REGISTRY.itemGroup(ItemButterflyGE::new, "_ge")
	//TODO: Clean up feature groups so we can use affix at items too
	public static final FeatureItem<ItemButterflyGE> BUTTERFLY_GE = REGISTRY.item(() -> new ItemButterflyGE(EnumFlutterType.BUTTERFLY), "butterfly_ge");
	public static final FeatureItem<ItemButterflyGE> SERUM_GE = REGISTRY.item(() -> new ItemButterflyGE(EnumFlutterType.SERUM), "serum_ge");
	public static final FeatureItem<ItemButterflyGE> CATERPILLAR_GE = REGISTRY.item(() -> new ItemButterflyGE(EnumFlutterType.CATERPILLAR), "caterpillar_ge");
	public static final FeatureItem<ItemButterflyGE> COCOON_GE = REGISTRY.item(() -> new ItemButterflyGE(EnumFlutterType.COCOON), "cocoon_ge");

	public static final FeatureItem<SpawnEggItem> BUTTERFLY_SPAWN_EGG = REGISTRY.item(() -> new SpawnEggItem(LepidopterologyEntities.BUTTERFLY.entityType(), 0x000000, 0xffffff, (new Item.Properties()).group(ItemGroup.MISC)), "butterfly_spawn_egg");

	private LepidopterologyItems() {
	}
}
