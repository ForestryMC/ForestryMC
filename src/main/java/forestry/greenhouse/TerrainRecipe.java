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

import forestry.api.climate.IClimateInfo;
import forestry.api.greenhouse.ITerrainRecipe;
import net.minecraft.block.state.IBlockState;

public abstract class TerrainRecipe implements ITerrainRecipe {

	public IBlockState result;
	public IClimateInfo minClimate,  maxClimate;
	public float chance;
	
	public TerrainRecipe(IBlockState result, IClimateInfo minClimate, IClimateInfo maxClimate, float chance) {
		this.result = result;
		this.minClimate = minClimate;
		this.maxClimate = maxClimate;
		this.chance = chance;
	}

	@Override
	public IBlockState getResult() {
		return result;
	}

	@Override
	public IClimateInfo getMinClimate() {
		return minClimate;
	}

	@Override
	public IClimateInfo getMaxClimate() {
		return maxClimate;
	}
	
	@Override
	public float getChance() {
		return chance;
	}

}
