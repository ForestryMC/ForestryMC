package forestry.worktable.blocks;

import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.MachineProperties;
import forestry.core.tiles.TileForestry;
import forestry.worktable.tiles.TileWorktable;

public enum BlockTypeWorktable implements IBlockType {
	WORKTABLE(TileWorktable.class, "worktable");

	public static final BlockTypeWorktable[] VALUES = values();

	private final IMachineProperties machineProperties;

	<T extends TileForestry> BlockTypeWorktable(Class<T> teClass, String name) {
		this.machineProperties = new MachineProperties<>(teClass, name);
	}

	@Override
	public IMachineProperties getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getName() {
		return getMachineProperties().getName();
	}
}
