package forestry.worktable.blocks;

import forestry.core.blocks.BlockRegistry;
import forestry.core.items.ItemBlockNBT;

public class BlockRegistryWorktable extends BlockRegistry {
	public final BlockWorktable worktable;

	public BlockRegistryWorktable() {
		worktable = new BlockWorktable(BlockTypeWorktable.WORKTABLE);
		registerBlock(worktable, new ItemBlockNBT(worktable), "worktable");
	}
}
