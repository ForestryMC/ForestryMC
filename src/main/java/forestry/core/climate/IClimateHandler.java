package forestry.core.climate;

import forestry.api.core.climate.IClimateMap;
import forestry.api.core.climate.IClimateWorld;
import forestry.api.core.climate.IClimatedPosition;
import net.minecraft.util.math.BlockPos;

public interface IClimateHandler {

	BlockPos getPos();
	
	IClimateWorld getWorld();
	
	boolean canHoldClimate(IClimatedPosition position);
	
}
