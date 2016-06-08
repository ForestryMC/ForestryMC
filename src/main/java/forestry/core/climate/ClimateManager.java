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
package forestry.core.climate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import forestry.api.core.ForestryAPI;
import forestry.api.core.climate.IClimateManager;
import forestry.api.core.climate.IClimateWorld;
import forestry.api.core.climate.IClimateWorld.ClimateChunk;
import forestry.api.greenhouse.GreenhouseManager;
import forestry.api.greenhouse.IGreenhouseState;
import forestry.arboriculture.PluginArboriculture;
import forestry.plugins.ForestryPluginUids;
import forestry.plugins.PluginManager;

public class ClimateManager implements IClimateManager{

	public Map<Integer, IClimateWorld> climateWorlds;
	
	public ClimateManager() {
		climateWorlds = new ConcurrentHashMap<>();
	}
	
	@Override
	public float getTemperature(World world, BlockPos pos) {
		if(ForestryAPI.enabledPlugins.contains(ForestryPluginUids.GREENHOUSE)){
			IGreenhouseState state = GreenhouseManager.greenhouseHelper.getGreenhouseState(world, pos);
			
			if(state != null){
				return state.getExactTemperature();
			}
		}
		Biome biome = world.getBiome(pos);
		return biome.getTemperature();
	}

	@Override
	public float getHumidity(World world, BlockPos pos) {
		if(ForestryAPI.enabledPlugins.contains(ForestryPluginUids.GREENHOUSE)){
			IGreenhouseState state = GreenhouseManager.greenhouseHelper.getGreenhouseState(world, pos);
			
			if(state != null){
				return state.getExactHumidity();
			}
		}
		Biome biome = world.getBiome(pos);
		return biome.getRainfall();
	}
	
	@Override
	public void registerWorld(IClimateWorld climateWorld) {
		if(!climateWorlds.containsKey(Integer.valueOf(climateWorld.getDimensionID()))){
			climateWorlds.put(climateWorld.getDimensionID(), climateWorld);
		}
	}
	
	@Override
	public Map<Integer, IClimateWorld> getClimateWorlds() {
		return climateWorlds;
	}

}
