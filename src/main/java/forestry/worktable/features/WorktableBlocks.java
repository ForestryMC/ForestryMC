package forestry.worktable.features;

import forestry.core.items.ItemBlockForestry;
import forestry.core.items.ItemBlockNBT;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.worktable.ModuleWorktable;
import forestry.worktable.blocks.BlockTypeWorktable;
import forestry.worktable.blocks.BlockWorktable;

@FeatureProvider
public class WorktableBlocks {
    private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleWorktable.class);
    public static final FeatureBlock<BlockWorktable, ItemBlockForestry> WORKTABLE = REGISTRY.block(() -> new BlockWorktable(BlockTypeWorktable.WORKTABLE), ItemBlockNBT::new, "worktable");

    private WorktableBlocks() {
    }
}
