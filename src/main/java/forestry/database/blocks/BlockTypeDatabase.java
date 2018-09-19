package forestry.database.blocks;

import forestry.core.blocks.IBlockTypeCustom;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.MachineProperties;
import forestry.core.tiles.TileForestry;
import forestry.database.tiles.TileDatabase;

public enum BlockTypeDatabase implements IBlockTypeCustom {
	DATABASE(TileDatabase.class, "database");
	public static final BlockTypeDatabase[] VALUES = values();

	private final IMachineProperties machineProperties;

	<T extends TileForestry> BlockTypeDatabase(Class<T> teClass, String name) {
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
