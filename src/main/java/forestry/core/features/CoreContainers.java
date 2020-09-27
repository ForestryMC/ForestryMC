package forestry.core.features;

import forestry.core.ModuleCore;
import forestry.core.circuits.ContainerSolderingIron;
import forestry.core.gui.ContainerAlyzer;
import forestry.core.gui.ContainerAnalyzer;
import forestry.core.gui.ContainerEscritoire;
import forestry.core.gui.ContainerNaturalistInventory;
import forestry.modules.features.FeatureContainerType;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class CoreContainers {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleCore.class);

    public static final FeatureContainerType<ContainerAlyzer> ALYZER = REGISTRY.container(
            ContainerAlyzer::fromNetwork,
            "alyzer"
    );
    public static final FeatureContainerType<ContainerAnalyzer> ANALYZER = REGISTRY.container(
            ContainerAnalyzer::fromNetwork,
            "analyzer"
    );
    public static final FeatureContainerType<ContainerEscritoire> ESCRITOIRE = REGISTRY.container(
            ContainerEscritoire::fromNetwork,
            "escritoire"
    );
    public static final FeatureContainerType<ContainerNaturalistInventory> NATURALIST_INVENTORY = REGISTRY.container(
            ContainerNaturalistInventory::fromNetwork,
            "naturalist_inventory"
    );
    public static final FeatureContainerType<ContainerSolderingIron> SOLDERING_IRON = REGISTRY.container(
            ContainerSolderingIron::fromNetwork,
            "soldering_iron"
    );
}
