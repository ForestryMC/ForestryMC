package forestry.climatology.features;

import forestry.climatology.ModuleClimatology;
import forestry.climatology.gui.ContainerHabitatFormer;
import forestry.modules.features.FeatureContainerType;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class ClimatologyContainers {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleClimatology.class);

    public static final FeatureContainerType<ContainerHabitatFormer> HABITAT_FORMER = REGISTRY.container(
            ContainerHabitatFormer::fromNetwork,
            "habitat_former"
    );

}
