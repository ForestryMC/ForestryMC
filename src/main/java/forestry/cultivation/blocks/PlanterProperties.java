package forestry.cultivation.blocks;

import forestry.core.blocks.MachineProperties;
import forestry.cultivation.tiles.TilePlanter;

public class PlanterProperties<T extends TilePlanter> extends MachineProperties<T> {

	PlanterProperties(Class<T> teClass, String name) {
		super(teClass, name);
	}
}
