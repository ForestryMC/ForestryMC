package forestry.cultivation.blocks;

import forestry.core.blocks.MachineProperties;
import forestry.cultivation.tiles.TilePlanter;
import forestry.modules.features.FeatureTileType;

import java.util.function.Supplier;

public class PlanterProperties<T extends TilePlanter> extends MachineProperties<T> {

    PlanterProperties(Supplier<FeatureTileType<? extends T>> teClass, String name) {
		super(teClass, name);
	}
}
