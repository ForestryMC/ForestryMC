package forestry.worktable.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileRegistry;
import forestry.worktable.features.WorktableBlocks;

public class TileRegistryWorktable extends TileRegistry {

	public final TileEntityType<TileWorktable> WORKTABLE;

	public TileRegistryWorktable() {
		WORKTABLE = registerTileEntityType(TileWorktable::new, "worktable", WorktableBlocks.WORKTABLE.block());
	}

}
