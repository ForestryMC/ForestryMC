package forestry.worktable.tiles;

import net.minecraft.tileentity.TileEntityType;

import forestry.core.tiles.TileRegistry;
import forestry.worktable.ModuleWorktable;
import forestry.worktable.blocks.BlockRegistryWorktable;

public class TileRegistryWorktable extends TileRegistry {

	public final TileEntityType<TileWorktable> WORKTABLE;

	public TileRegistryWorktable() {
		BlockRegistryWorktable blocks = ModuleWorktable.getBlocks();

		WORKTABLE = registerTileEntityType(TileWorktable::new, "worktable", blocks.worktable);
	}

}
