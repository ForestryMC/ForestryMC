package forestry.worktable.features;

import forestry.modules.features.FeatureContainerType;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.worktable.ModuleWorktable;
import forestry.worktable.gui.ContainerWorktable;

@FeatureProvider
public class WorktableContainers {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleWorktable.class);

    public static final FeatureContainerType<ContainerWorktable> WORKTABLE = REGISTRY.container(ContainerWorktable::fromNetwork, "worktable");

}
