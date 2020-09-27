package forestry.sorting.features;

import forestry.modules.features.FeatureContainerType;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.sorting.ModuleSorting;
import forestry.sorting.gui.ContainerGeneticFilter;

@FeatureProvider
public class SortingContainers {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleSorting.class);

    public static final FeatureContainerType<ContainerGeneticFilter> GENETIC_FILTER = REGISTRY.container(
            ContainerGeneticFilter::fromNetwork,
            "genetic_filter"
    );

}
