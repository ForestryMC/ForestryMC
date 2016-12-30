/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.greenhouse;

import javax.annotation.Nullable;
import java.util.List;

import forestry.api.climate.IClimateInfo;
import forestry.api.multiblock.IGreenhouseController;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IGreenhouseHelper {

	/**
	 * @return A {@link IGreenhouseController} of a greenhouse, when the pos is a greenhouse
	 */
	@Nullable
	IGreenhouseController getGreenhouseController(World world, BlockPos pos);

	void registerGreenhouseLogic(Class<? extends IGreenhouseLogic> logic);

	List<Class<? extends IGreenhouseLogic>> getGreenhouseLogics();
	
	void registerTerrainRecipe(Block inputBlock, IBlockState outputState, IClimateInfo minClimate, IClimateInfo maxClimate, float chance);
	
	void registerTerrainRecipe(IBlockState inputState, IBlockState outputState, IClimateInfo minClimate, IClimateInfo maxClimate, float chance);
	
	ITerrainRecipe getValidTerrainRecipe(IBlockState blockState, IClimateInfo climateInfo);

}
