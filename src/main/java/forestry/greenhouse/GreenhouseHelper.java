/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.greenhouse;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

import forestry.api.climate.IClimateInfo;
import forestry.api.greenhouse.IGreenhouseHelper;
import forestry.api.greenhouse.IGreenhouseLogic;
import forestry.api.greenhouse.IInternalBlock;
import forestry.api.greenhouse.ITerrainRecipe;
import forestry.api.multiblock.IGreenhouseController;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.multiblock.MultiblockRegistry;
import forestry.core.utils.ClimateUtil;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.multiblock.InternalBlockCheck;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GreenhouseHelper implements IGreenhouseHelper {

	private final List<Class<? extends IGreenhouseLogic>> greenhouseLogics = new ArrayList<>();
	private final List<ITerrainRecipe> terrainRecipes = new ArrayList<>();

	@Override
	@Nullable
	public IGreenhouseController getGreenhouseController(World world, BlockPos pos) {
		for (IMultiblockControllerInternal controllerInternal : MultiblockRegistry.getControllersFromWorld(world)) {
			if (controllerInternal instanceof IGreenhouseControllerInternal &&
					controllerInternal.isAssembled() &&
					isPositionInGreenhouse((IGreenhouseControllerInternal) controllerInternal, pos)) {
				return (IGreenhouseController) controllerInternal;
			}
		}
		return null;
	}

	private static boolean isPositionInGreenhouse(IGreenhouseControllerInternal controller, BlockPos pos) {
		IInternalBlock checkBlock = new InternalBlockCheck(pos);
		return controller.getInternalBlocks().contains(checkBlock);
	}

	@Override
	public void registerGreenhouseLogic(Class<? extends IGreenhouseLogic> logic) {
		if (!greenhouseLogics.contains(logic)) {
			greenhouseLogics.add(logic);
		}
	}

	@Override
	public List<Class<? extends IGreenhouseLogic>> getGreenhouseLogics() {
		return greenhouseLogics;
	}
	
	@Override
	public void registerTerrainRecipe(IBlockState inputState, IBlockState outputState, IClimateInfo minClimate, IClimateInfo maxClimate, float chance) {
		Preconditions.checkNotNull(inputState);
		Preconditions.checkNotNull(outputState);
		Preconditions.checkNotNull(minClimate);
		Preconditions.checkNotNull(maxClimate);
		terrainRecipes.add(new TerrainRecipeState(inputState, outputState, minClimate, maxClimate, chance));
	}
	
	@Override
	public void registerTerrainRecipe(Block inputBlock, IBlockState outputState, IClimateInfo minClimate, IClimateInfo maxClimate, float chance) {
		Preconditions.checkNotNull(inputBlock);
		Preconditions.checkNotNull(outputState);
		Preconditions.checkNotNull(minClimate);
		Preconditions.checkNotNull(maxClimate);
		terrainRecipes.add(new TerrainRecipeBlock(inputBlock, outputState, minClimate, maxClimate, chance));
	}
	
	@Override
	public ITerrainRecipe getValidTerrainRecipe(IBlockState blockState, IClimateInfo climateInfo) {
		for(ITerrainRecipe recipe : terrainRecipes){
			if(recipe.matches(blockState)){
				if(ClimateUtil.isWithinLimits(recipe.getMinClimate(), recipe.getMaxClimate(), climateInfo)){
					return recipe;
				}
			}
		}
		return null;
	}

}
