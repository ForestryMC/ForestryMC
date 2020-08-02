package forestry.worktable.features;

import forestry.modules.features.FeatureProvider;
import forestry.modules.features.FeatureTileType;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.worktable.ModuleWorktable;
import forestry.worktable.tiles.TileWorktable;

@FeatureProvider
public class WorktableTiles {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleWorktable.class);

    public static final FeatureTileType<TileWorktable> WORKTABLE = REGISTRY.tile(TileWorktable::new, "worktable", WorktableBlocks.WORKTABLE::collect);
}
