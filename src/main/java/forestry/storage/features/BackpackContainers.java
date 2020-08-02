package forestry.storage.features;

import forestry.modules.features.FeatureContainerType;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.storage.ModuleBackpacks;
import forestry.storage.gui.ContainerBackpack;
import forestry.storage.gui.ContainerNaturalistBackpack;

@FeatureProvider
public class BackpackContainers {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleBackpacks.class);

    public static final FeatureContainerType<ContainerBackpack> BACKPACK = REGISTRY.container(ContainerBackpack::fromNetwork, "backpack");
    public static final FeatureContainerType<ContainerNaturalistBackpack> NATURALIST_BACKPACK = REGISTRY.container(ContainerNaturalistBackpack::fromNetwork, "naturalist_backpack");
}
