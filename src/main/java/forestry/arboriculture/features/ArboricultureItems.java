package forestry.arboriculture.features;

import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.items.ItemGermlingGE;
import forestry.arboriculture.items.ItemGrafter;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class ArboricultureItems {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleArboriculture.class);

    public static final FeatureItem<ItemGermlingGE> SAPLING = REGISTRY.item(
            () -> new ItemGermlingGE(EnumGermlingType.SAPLING),
            "sapling"
    );
    public static final FeatureItem<ItemGermlingGE> POLLEN_FERTILE = REGISTRY.item(() -> new ItemGermlingGE(
            EnumGermlingType.POLLEN), "pollen_fertile");
    public static final FeatureItem<ItemGrafter> GRAFTER = REGISTRY.item(() -> new ItemGrafter(9), "grafter");
    public static final FeatureItem<ItemGrafter> GRAFTER_PROVEN = REGISTRY.item(
            () -> new ItemGrafter(149),
            "grafter_proven"
    );

    public ArboricultureItems() {

    }
}
