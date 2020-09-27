package forestry.apiculture.features;

import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.gui.*;
import forestry.modules.features.FeatureContainerType;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class ApicultureContainers {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleApiculture.class);

    public static final FeatureContainerType<ContainerAlveary> ALVEARY = REGISTRY.container(
            ContainerAlveary::fromNetwork,
            "alveary"
    );
    public static final FeatureContainerType<ContainerAlvearyHygroregulator> ALVEARY_HYGROREGULATOR = REGISTRY.container(
            ContainerAlvearyHygroregulator::fromNetwork,
            "alveary_hygroregulator"
    );
    public static final FeatureContainerType<ContainerAlvearySieve> ALVEARY_SIEVE = REGISTRY.container(
            ContainerAlvearySieve::fromNetwork,
            "alveary_sieve"
    );
    public static final FeatureContainerType<ContainerAlvearySwarmer> ALVEARY_SWARMER = REGISTRY.container(
            ContainerAlvearySwarmer::fromNetwork,
            "alveary_swarmer"
    );
    public static final FeatureContainerType<ContainerBeeHousing> BEE_HOUSING = REGISTRY.container(
            ContainerBeeHousing::fromNetwork,
            "bee_housing"
    );
    public static final FeatureContainerType<ContainerHabitatLocator> HABITAT_LOCATOR = REGISTRY.container(
            ContainerHabitatLocator::fromNetwork,
            "habitat_locator"
    );
    public static final FeatureContainerType<ContainerImprinter> IMPRINTER = REGISTRY.container(
            ContainerImprinter::fromNetwork,
            "imprinter"
    );
    public static final FeatureContainerType<ContainerMinecartBeehouse> BEEHOUSE_MINECART = REGISTRY.container(
            ContainerMinecartBeehouse::fromNetwork,
            "minecart_beehouse"
    );
}
