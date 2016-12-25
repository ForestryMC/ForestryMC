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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import forestry.api.climate.IClimateInfo;
import forestry.api.climate.IClimateManager;
import forestry.api.climate.IClimatePosition;
import forestry.api.climate.IClimateProvider;
import forestry.api.climate.IClimateRegion;
import forestry.api.climate.IClimateSourceProvider;
import forestry.api.core.ForestryAPI;
import forestry.core.DefaultClimateProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClimateManager implements IClimateManager {

	protected final Map<World, List<IClimateRegion>> regions;
	private final Object regionsMutex;

	public ClimateManager() {
		regions = new HashMap<>();
		regionsMutex = new Object();
	}

	@Override
	public void addRegion(IClimateRegion region) {
		synchronized (regionsMutex) {
			World world = region.getWorld();
			if (!regions.containsKey(world)) {
				this.regions.put(world, new ArrayList<>());
			}
			List<IClimateRegion> regions = this.regions.get(world);
			if (!regions.contains(region)) {
				if(!regions.isEmpty()){
					for (IClimatePosition pos : region.getPositions()) {
						if (getRegionForPos(world, pos.getPos()) != null) {
							return;
						}
					}
				}
				regions.add(region);
			}
		}
	}

	@Override
	public void removeRegion(IClimateRegion region) {
		synchronized (regionsMutex) {
			World world = region.getWorld();
			if (!regions.containsKey(world)) {
				this.regions.put(world, new ArrayList<>());
			}
			List<IClimateRegion> regions = this.regions.get(world);
			regions.remove(region);
		}
	}

	@Override
	public void removeSource(IClimateSourceProvider source) {
		synchronized (regionsMutex) {
			World world = source.getWorldObj();
			if (!regions.containsKey(world)) {
				regions.put(world, new ArrayList<>());
			}
			IClimateRegion region = getRegionForPos(source.getWorldObj(), source.getCoordinates());
			if (region != null) {
				if (region.getSources().contains(source.getClimateSource())) {
					region.removeSource(source.getClimateSource());
				}
			}
		}
	}

	@Override
	public void addSource(IClimateSourceProvider source) {
		synchronized (regionsMutex) {
			World world = source.getWorldObj();
			if (!regions.containsKey(world)) {
				regions.put(world, new ArrayList<>());
			}
			IClimateRegion region = getRegionForPos(source.getWorldObj(), source.getCoordinates());
			if (region != null) {
				if (!region.getSources().contains(source.getClimateSource())) {
					region.addSource(source.getClimateSource());
				}
			}
		}
	}
	
	@Override
	public IClimateInfo createInfo(float temperature, float humidity) {
		return new ClimateInfo(temperature, humidity);
	}
	
	@Override
	public IClimateInfo getInfo(World world, BlockPos pos) {
		IClimatePosition position = ForestryAPI.climateManager.getPosition(world, pos);

		if (position != null) {
			return position.getInfo();
		}
		return BiomeClimateInfo.getInfo(world.getBiome(pos));
	}

	@Override
	public Map<World, List<IClimateRegion>> getRegions() {
		return regions;
	}

	@Override
	public IClimatePosition getPosition(World world, BlockPos pos) {
		if (!regions.containsKey(world)) {
			regions.put(world, new ArrayList<>());
			return null;
		}
		for (IClimateRegion region : regions.get(world)) {
			IClimatePosition position = region.getPosition(pos);
			if(position != null){
				return position;
			}
		}
		return null;
	}

	@Override
	public IClimateRegion getRegionForPos(World world, BlockPos pos) {
		if (!regions.containsKey(world)) {
			this.regions.put(world, new ArrayList<>());
		}
		List<IClimateRegion> regions = this.regions.get(world);
		for (IClimateRegion region : regions) {
			if (region.getPosition(pos) != null) {
				return region;
			}
		}
		return null;
	}
	
	@Override
	public void onWorldUnload(World world) {
		regions.remove(world);
	}

	@Override
	public IClimateProvider getDefaultClimate(World world, BlockPos pos) {
		return new DefaultClimateProvider(world, pos);
	}

}
