package forestry.farming.features;

import forestry.farming.ModuleFarming;
import forestry.farming.gui.ContainerFarm;
import forestry.modules.features.FeatureContainerType;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class FarmingContainers {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleFarming.class);

    public static final FeatureContainerType<ContainerFarm> FARM = REGISTRY.container(ContainerFarm::fromNetwork, "farm");

}
