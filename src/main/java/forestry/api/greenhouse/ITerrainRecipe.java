package forestry.api.greenhouse;

import forestry.api.climate.IClimateInfo;
import net.minecraft.block.state.IBlockState;

public interface ITerrainRecipe {
	
	IBlockState getInput();
	
	IBlockState getResult();
	
	IClimateInfo getMinClimate();
	
	IClimateInfo getMaxClimate();

}
