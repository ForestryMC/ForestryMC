package forestry.sorting.features;

import forestry.core.items.ItemBlockForestry;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.sorting.ModuleSorting;
import forestry.sorting.blocks.BlockGeneticFilter;

@FeatureProvider
public class SortingBlocks {

    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleSorting.class);

    public static final FeatureBlock<BlockGeneticFilter, ItemBlockForestry> FILTER = REGISTRY.block(BlockGeneticFilter::new, ItemBlockForestry::new, "genetic_filter");

    private SortingBlocks() {
    }
}
