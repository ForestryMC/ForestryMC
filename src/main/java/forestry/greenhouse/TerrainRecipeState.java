package forestry.greenhouse;

import forestry.api.climate.IClimateInfo;
import net.minecraft.block.state.IBlockState;

public class TerrainRecipeState extends TerrainRecipe {

	private IBlockState input;
	
	public TerrainRecipeState(IBlockState input, IBlockState result, IClimateInfo minClimate, IClimateInfo maxClimate, float chance) {
		super(result, minClimate, maxClimate, chance);
		this.input = input;
	}

	@Override
	public boolean matches(IBlockState blockState) {
		return blockState == input;
	}
	
}
