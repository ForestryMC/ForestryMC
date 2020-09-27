package forestry.climatology.features;

import forestry.climatology.ModuleClimatology;
import forestry.climatology.blocks.BlockHabitatFormer;
import forestry.core.items.ItemBlockForestry;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class ClimatologyBlocks {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleClimatology.class);

    public static final FeatureBlock<BlockHabitatFormer, ItemBlockForestry> HABITATFORMER = REGISTRY.block(
            BlockHabitatFormer::new,
            ItemBlockForestry::new,
            "habitat_former"
    );

    private ClimatologyBlocks() {

    }
}
