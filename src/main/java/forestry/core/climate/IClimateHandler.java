package forestry.core.climate;

import forestry.api.core.climate.IClimateWorld;
import forestry.api.core.climate.IClimatedPosition;
import net.minecraft.util.math.BlockPos;

public interface IClimateHandler {

	BlockPos getPos();
	
	IClimateWorld getWorld();
	
	boolean canHandle(IClimatedPosition position);
	
	void updateClimate(IClimatedPosition position);
	
	boolean canHoldClimate(IClimatedPosition position);
	
}
