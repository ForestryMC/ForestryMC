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
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import forestry.api.core.ForestryAPI;
import forestry.api.core.climate.IClimateManager;
import forestry.api.core.climate.IClimateWorld;

public class ClimateManager implements IClimateManager{

	public Map<Integer, IClimateWorld> climateWorlds;
	
	public ClimateManager() {
		climateWorlds = new ConcurrentHashMap<>();
	}
	
	@Override
	public float getTemperature(World world, BlockPos pos) {
		IClimateWorld climateWorld = getOrCreateWorld(world);
		Biome biome = world.getBiome(pos);
		
		if(climateWorld.getPosition(pos) != null){
			return climateWorld.getPosition(pos).getTemperature();
		}
		
		return biome.getTemperature();
	}

	@Override
	public float getHumidity(World world, BlockPos pos) {
		IClimateWorld climateWorld = getOrCreateWorld(world);
		Biome biome = world.getBiome(pos);
		
		if(climateWorld.getPosition(pos) != null){
			return climateWorld.getPosition(pos).getHumidity();
		}
		
		return biome.getRainfall();
	}
	
	@Override
	public void registerWorld(IClimateWorld climateWorld) {
		if(!climateWorlds.containsKey(Integer.valueOf(climateWorld.getDimensionID()))){
			climateWorlds.put(climateWorld.getDimensionID(), climateWorld);
		}
	}
	
	public static IClimateWorld getOrCreateWorld(World world){
		if(world == null){
			return null;
		}
		 IClimateWorld climateWorld = ForestryAPI.climateManager.getClimateWorlds().get(Integer.valueOf(world.provider.getDimension()));
		 
		 if(climateWorld == null){
			 climateWorld = new ClimateWorld();
			 ForestryAPI.climateManager.registerWorld(climateWorld);
		 }
		 return climateWorld;
	}
	
	
	@Override
	public Map<Integer, IClimateWorld> getClimateWorlds() {
		return climateWorlds;
	}

}
