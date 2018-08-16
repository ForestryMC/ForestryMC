package forestry.climatology.blocks;

import forestry.climatology.tiles.TileHabitatformer;
import forestry.core.blocks.IBlockTypeCustom;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.MachineProperties;
import forestry.core.tiles.TileForestry;

public enum BlockTypeClimatology implements IBlockTypeCustom {
	HABITAT_FORMER(TileHabitatformer.class, "habitat_former");

	private final IMachineProperties machineProperties;

	<T extends TileForestry> BlockTypeClimatology(Class<T> teClass, String name) {
		this.machineProperties = new MachineProperties<>(teClass, name);
	}

	@Override
	public IMachineProperties<?> getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getName() {
		return getMachineProperties().getName();
	}
}
