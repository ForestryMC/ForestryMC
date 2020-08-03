package forestry.factory.features;

import forestry.factory.ModuleFactory;
import forestry.factory.gui.*;
import forestry.modules.features.FeatureContainerType;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class FactoryContainers {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleFactory.class);

    public static final FeatureContainerType<ContainerBottler> BOTTLER = REGISTRY.container(ContainerBottler::fromNetwork, "bottler");
    public static final FeatureContainerType<ContainerCarpenter> CARPENTER = REGISTRY.container(ContainerCarpenter::fromNetwork, "carpenter");
    public static final FeatureContainerType<ContainerCentrifuge> CENTRIFUGE = REGISTRY.container(ContainerCentrifuge::fromNetwork, "centrifuge");
    public static final FeatureContainerType<ContainerFabricator> FABRICATOR = REGISTRY.container(ContainerFabricator::fromNetwork, "fabricator");
    public static final FeatureContainerType<ContainerFermenter> FERMENTER = REGISTRY.container(ContainerFermenter::fromNetwork, "fermenter");
    public static final FeatureContainerType<ContainerMoistener> MOISTENER = REGISTRY.container(ContainerMoistener::fromNetwork, "moistener");
    public static final FeatureContainerType<ContainerRaintank> RAINTANK = REGISTRY.container(ContainerRaintank::fromNetwork, "raintank");
    public static final FeatureContainerType<ContainerSqueezer> SQUEEZER = REGISTRY.container(ContainerSqueezer::fromNetwork, "squeezer");
    public static final FeatureContainerType<ContainerStill> STILL = REGISTRY.container(ContainerStill::fromNetwork, "still");
}
