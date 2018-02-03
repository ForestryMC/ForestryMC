package forestry.cultivation.blocks;

import forestry.core.blocks.IBlockTypeCustom;
import forestry.core.blocks.IMachineProperties;
import forestry.cultivation.tiles.TileArboretum;
import forestry.cultivation.tiles.TileBog;
import forestry.cultivation.tiles.TileFarmCrops;
import forestry.cultivation.tiles.TileFarmEnder;
import forestry.cultivation.tiles.TileFarmGourd;
import forestry.cultivation.tiles.TileFarmMushroom;
import forestry.cultivation.tiles.TileFarmNether;
import forestry.cultivation.tiles.TilePlanter;

public enum BlockTypePlanter implements IBlockTypeCustom {
	ARBORETUM(TileArboretum.class, "arboretum"),
	FARM_CROPS(TileFarmCrops.class, "farm_crop"),
	FARM_MUSHROOM(TileFarmMushroom.class, "farm_mushroom"),
	FARM_GOURD(TileFarmGourd.class, "farm_gourd"),
	FARM_NETHER(TileFarmNether.class, "farm_nether"),
	FARM_ENDER(TileFarmEnder.class, "farm_ender"),
	PEAT_POG(TileBog.class, "peat_bog"),

	//TODO Add ic2 integration
	/*PLANTATION(TilePlantation.class, "plantation")*/;

	private final IMachineProperties machineProperties;

	BlockTypePlanter(Class<? extends TilePlanter> teClass, String name) {
		this.machineProperties = new PlanterProperties<>(teClass, name);
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
