package forestry.storage.features;

import forestry.api.core.ItemGroups;
import forestry.api.storage.EnumBackpackType;
import forestry.apiculture.genetics.BeeRoot;
import forestry.lepidopterology.genetics.ButterflyRoot;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.storage.ModuleBackpacks;

@FeatureProvider
public class BackpackItems {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleBackpacks.class);

	public static final FeatureItem<?> APIARIST_BACKPACK = REGISTRY.naturalistBackpack(ModuleBackpacks.APIARIST, BeeRoot.UID, ItemGroups.tabApiculture, "apiarist_bag");
	public static final FeatureItem<?> LEPIDOPTERIST_BACKPACK = REGISTRY.naturalistBackpack(ModuleBackpacks.LEPIDOPTERIST, ButterflyRoot.UID, ItemGroups.tabLepidopterology, "lepidopterist_bag");

	public static final FeatureItem<?> MINER_BACKPACK = REGISTRY.backpack(ModuleBackpacks.MINER, EnumBackpackType.NORMAL, "miner_bag");
	public static final FeatureItem<?> MINER_BACKPACK_T_2 = REGISTRY.backpack(ModuleBackpacks.MINER, EnumBackpackType.WOVEN, "miner_bag_woven");
	public static final FeatureItem<?> DIGGER_BACKPACK = REGISTRY.backpack(ModuleBackpacks.DIGGER, EnumBackpackType.NORMAL, "digger_bag");
	public static final FeatureItem<?> DIGGER_BACKPACK_T_2 = REGISTRY.backpack(ModuleBackpacks.DIGGER, EnumBackpackType.WOVEN, "digger_bag_woven");
	public static final FeatureItem<?> FORESTER_BACKPACK = REGISTRY.backpack(ModuleBackpacks.FORESTER, EnumBackpackType.NORMAL, "forester_bag");
	public static final FeatureItem<?> FORESTER_BACKPACK_T_2 = REGISTRY.backpack(ModuleBackpacks.FORESTER, EnumBackpackType.WOVEN, "forester_bag_woven");
	public static final FeatureItem<?> HUNTER_BACKPACK = REGISTRY.backpack(ModuleBackpacks.HUNTER, EnumBackpackType.NORMAL, "hunter_bag");
	public static final FeatureItem<?> HUNTER_BACKPACK_T_2 = REGISTRY.backpack(ModuleBackpacks.HUNTER, EnumBackpackType.WOVEN, "hunter_bag_woven");
	public static final FeatureItem<?> ADVENTURER_BACKPACK = REGISTRY.backpack(ModuleBackpacks.ADVENTURER, EnumBackpackType.NORMAL, "adventurer_bag");
	public static final FeatureItem<?> ADVENTURER_BACKPACK_T_2 = REGISTRY.backpack(ModuleBackpacks.ADVENTURER, EnumBackpackType.WOVEN, "adventurer_bag_woven");
	public static final FeatureItem<?> BUILDER_BACKPACK = REGISTRY.backpack(ModuleBackpacks.BUILDER, EnumBackpackType.NORMAL, "builder_bag");
	public static final FeatureItem<?> BUILDER_BACKPACK_T_2 = REGISTRY.backpack(ModuleBackpacks.BUILDER, EnumBackpackType.WOVEN, "builder_bag_woven");

	private BackpackItems() {
	}
}
