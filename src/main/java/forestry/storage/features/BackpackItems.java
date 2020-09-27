package forestry.storage.features;

import forestry.api.core.ItemGroups;
import forestry.api.storage.BackpackManager;
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

    public static final FeatureItem APIARIST_BACKPACK = REGISTRY.naturalistBackpack(
            "apiarist",
            BeeRoot.UID,
            ItemGroups.tabApiculture,
            "apiarist_bag"
    );
    public static final FeatureItem LEPIDOPTERIST_BACKPACK = REGISTRY.naturalistBackpack(
            "lepidopterist",
            ButterflyRoot.UID,
            ItemGroups.tabLepidopterology,
            "lepidopterist_bag"
    );

    public static final FeatureItem MINER_BACKPACK = REGISTRY.backpack(
            BackpackManager.MINER_UID,
            EnumBackpackType.NORMAL,
            "miner_bag"
    );
    public static final FeatureItem MINER_BACKPACK_T_2 = REGISTRY.backpack(
            BackpackManager.MINER_UID,
            EnumBackpackType.WOVEN,
            "miner_bag_woven"
    );
    public static final FeatureItem DIGGER_BACKPACK = REGISTRY.backpack(
            BackpackManager.DIGGER_UID,
            EnumBackpackType.NORMAL,
            "digger_bag"
    );
    public static final FeatureItem DIGGER_BACKPACK_T_2 = REGISTRY.backpack(
            BackpackManager.DIGGER_UID,
            EnumBackpackType.WOVEN,
            "digger_bag_woven"
    );
    public static final FeatureItem FORESTER_BACKPACK = REGISTRY.backpack(
            BackpackManager.FORESTER_UID,
            EnumBackpackType.NORMAL,
            "forester_bag"
    );
    public static final FeatureItem FORESTER_BACKPACK_T_2 = REGISTRY.backpack(
            BackpackManager.FORESTER_UID,
            EnumBackpackType.WOVEN,
            "forester_bag_woven"
    );
    public static final FeatureItem HUNTER_BACKPACK = REGISTRY.backpack(
            BackpackManager.HUNTER_UID,
            EnumBackpackType.NORMAL,
            "hunter_bag"
    );
    public static final FeatureItem HUNTER_BACKPACK_T_2 = REGISTRY.backpack(
            BackpackManager.HUNTER_UID,
            EnumBackpackType.WOVEN,
            "hunter_bag_woven"
    );
    public static final FeatureItem ADVENTURER_BACKPACK = REGISTRY.backpack(
            BackpackManager.ADVENTURER_UID,
            EnumBackpackType.NORMAL,
            "adventurer_bag"
    );
    public static final FeatureItem ADVENTURER_BACKPACK_T_2 = REGISTRY.backpack(
            BackpackManager.ADVENTURER_UID,
            EnumBackpackType.WOVEN,
            "adventurer_bag_woven"
    );
    public static final FeatureItem BUILDER_BACKPACK = REGISTRY.backpack(
            BackpackManager.BUILDER_UID,
            EnumBackpackType.NORMAL,
            "builder_bag"
    );
    public static final FeatureItem BUILDER_BACKPACK_T_2 = REGISTRY.backpack(
            BackpackManager.BUILDER_UID,
            EnumBackpackType.WOVEN,
            "builder_bag_woven"
    );

    private BackpackItems() {
    }
}
