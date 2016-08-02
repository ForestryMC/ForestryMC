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
package forestry.core;

import forestry.api.core.IClimateProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import forestry.api.core.IClimateManager;
import forestry.api.greenhouse.GreenhouseManager;
import forestry.api.greenhouse.IGreenhouseState;

public class ClimateManager implements IClimateManager{

	@Override
	public float getTemperature(World world, BlockPos pos) {
		IGreenhouseState state = GreenhouseManager.greenhouseHelper.getGreenhouseState(world, pos);
		Biome biome = world.getBiome(pos);
		
		if(state != null){
			return state.getExactTemperature();
		}
		return biome.getTemperature();
	}

	@Override
	public float getHumidity(World world, BlockPos pos) {
		IGreenhouseState state = GreenhouseManager.greenhouseHelper.getGreenhouseState(world, pos);
		Biome biome = world.getBiome(pos);
		
		if(state != null){
			return state.getExactHumidity();
		}
		return biome.getRainfall();
	}

	@Override
	public IClimateProvider getDefaultClimate(World world, BlockPos pos) {
		return new DefaultClimateProvider(world, pos);
	}
}
