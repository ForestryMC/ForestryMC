package forestry.energy.features;

import forestry.energy.ModuleEnergy;
import forestry.energy.gui.ContainerEngineBiogas;
import forestry.energy.gui.ContainerEnginePeat;
import forestry.modules.features.FeatureContainerType;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class EnergyContainers {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleEnergy.class);

	public static final FeatureContainerType<ContainerEngineBiogas> ENGINE_BIOGAS = REGISTRY.container(ContainerEngineBiogas::fromNetwork, "engine_biogas");
	public static final FeatureContainerType<ContainerEnginePeat> ENGINE_PEAT = REGISTRY.container(ContainerEnginePeat::fromNetwork, "engine_peat");
}
