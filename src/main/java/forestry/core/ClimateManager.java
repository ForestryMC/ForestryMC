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

import forestry.api.core.IClimateManager;
import forestry.api.greenhouse.GreenhouseManager;
import forestry.api.greenhouse.IGreenhouseState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class ClimateManager implements IClimateManager{

	@Override
	public float getTemperature(World world, BlockPos pos) {
		IGreenhouseState state = GreenhouseManager.greenhouseHelper.getGreenhouseState(world, pos);
		BiomeGenBase biome = world.getBiomeGenForCoords(pos);
		
		if(state != null){
			return state.getExactTemperature();
		}
		return biome.temperature;
	}

	@Override
	public float getHumidity(World world, BlockPos pos) {
		IGreenhouseState state = GreenhouseManager.greenhouseHelper.getGreenhouseState(world, pos);
		BiomeGenBase biome = world.getBiomeGenForCoords(pos);
		
		if(state != null){
			return state.getExactHumidity();
		}
		return biome.rainfall;
	}

}
