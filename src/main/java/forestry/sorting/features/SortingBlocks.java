package forestry.sorting.features;

import forestry.core.config.Constants;
import forestry.core.items.ItemBlockForestry;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.sorting.blocks.BlockGeneticFilter;

public class SortingBlocks {

	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.SORTING);

	public static final FeatureBlock<BlockGeneticFilter, ItemBlockForestry> FILTER = REGISTRY.block(BlockGeneticFilter::new, ItemBlockForestry::new, "genetic_filter");

	private SortingBlocks() {
	}
}
