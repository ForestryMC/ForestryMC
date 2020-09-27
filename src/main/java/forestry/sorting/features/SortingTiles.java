package forestry.sorting.features;

import forestry.modules.features.FeatureProvider;
import forestry.modules.features.FeatureTileType;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.sorting.ModuleSorting;
import forestry.sorting.tiles.TileGeneticFilter;

@FeatureProvider
public class SortingTiles {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleSorting.class);

    public static final FeatureTileType<TileGeneticFilter> GENETIC_FILTER = REGISTRY.tile(
            TileGeneticFilter::new,
            "genetic_filter",
            SortingBlocks.FILTER::collect
    );
}
