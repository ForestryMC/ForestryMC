package forestry.greenhouse;

import forestry.api.climate.IClimateInfo;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class TerrainRecipeBlock extends TerrainRecipe {

	private Block input;
	
	public TerrainRecipeBlock(Block input, IBlockState result, IClimateInfo minClimate, IClimateInfo maxClimate, float chance) {
		super(result, minClimate, maxClimate, chance);
		this.input = input;
	}

	@Override
	public boolean matches(IBlockState blockState) {
		return blockState.getBlock() == input;
	}
	
}
