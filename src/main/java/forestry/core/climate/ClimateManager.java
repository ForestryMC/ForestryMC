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

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import forestry.api.climate.IClimateInfo;
import forestry.api.climate.IClimateManager;
import forestry.api.climate.IClimateProvider;
import forestry.api.climate.IClimateState;
import forestry.core.DefaultClimateProvider;
import forestry.core.utils.World2ObjectMap;

public class ClimateManager implements IClimateManager {

	private static final ClimateManager INSTANCE = new ClimateManager();

	private static final Map<Biome, IClimateState> BIOME_STATES = new HashMap<>();
	
	private final World2ObjectMap<ClimateWorldManager> managers;

	private ClimateManager() {
		managers = new World2ObjectMap(world->new ClimateWorldManager(this));
	}
	
	public static ClimateManager getInstance(){
		return INSTANCE;
	}

	@Override
	public IClimateInfo createInfo(float temperature, float humidity) {
		return new ClimateInfo(temperature, humidity);
	}

	@Override
	public IClimateInfo getInfo(World world, BlockPos pos) {
		return new ClimateInfo(getBiomeState(world, pos));
	}

	@Override
	public IClimateState getBiomeState(World world, BlockPos pos) {
		Biome biome = world.getBiome(pos);
		return getBiomeState(biome);
	}

	private IClimateState getBiomeState(Biome biome) {
		if (!BIOME_STATES.containsKey(biome)) {
			BIOME_STATES.put(biome, ClimateStates.of(biome.getDefaultTemperature(), biome.getRainfall()));
		}
		return BIOME_STATES.get(biome);
	}
	
	@Override
	public IClimateState getClimateState(World world, BlockPos pos) {
		ClimateWorldManager manager = managers.get(world);
		if(manager == null){
			return getBiomeState(world, pos);
		}
		return manager.getClimateState(world, pos);
	}

	@Override
	public IClimateProvider getDefaultClimate(World world, BlockPos pos) {
		return new DefaultClimateProvider(world, pos);
	}

}
