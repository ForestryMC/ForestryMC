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

import forestry.api.climate.IClimateManager;
import forestry.api.climate.IClimatePosition;
import forestry.api.climate.IClimateProvider;
import forestry.api.climate.IClimateRegion;
import forestry.api.climate.IClimateSourceProvider;
import forestry.core.DefaultClimateProvider;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class ClimateManager implements IClimateManager{

	protected Map<Integer, List<IClimateRegion>> regions;
	private final Object regionsMutex;

	public ClimateManager() {
		regions = new HashMap<>();
		regionsMutex = new Object();
	}

	@Override
	public void addRegion(IClimateRegion region) {
		if(region == null){
			return;
		}
		synchronized (regionsMutex) {
			Integer dimensionID = Integer.valueOf(region.getWorld().provider.getDimension());
			if(!regions.containsKey(dimensionID)){
				this.regions.put(dimensionID, new ArrayList<>());
			}
			List<IClimateRegion> regions = this.regions.get(dimensionID);
			if(!regions.contains(region)){
				for(BlockPos pos : region.getPositions().keySet()){
					if(getRegionForPos(region.getWorld(), pos) != null){
						return;
					}
				}
				regions.add(region);
			}
		}
	}

	@Override
	public void removeRegion(IClimateRegion region) {
		if(region == null){
			return;
		}
		synchronized (regionsMutex) {
			Integer dimensionID = Integer.valueOf(region.getWorld().provider.getDimension());
			if(!regions.containsKey(dimensionID)){
				this.regions.put(dimensionID, new ArrayList<>());
			}
			List<IClimateRegion> regions = this.regions.get(dimensionID);
			if(regions.contains(region)){
				regions.remove(region);
			}
		}
	}

	@Override
	public void removeSource(IClimateSourceProvider source) {
		if(source == null){
			return;
		}
		synchronized (regionsMutex) {
			Integer dimensionID = Integer.valueOf(source.getWorldObj().provider.getDimension());
			if(!regions.containsKey(dimensionID)){
				regions.put(dimensionID, new ArrayList<>());
			}
			IClimateRegion region = getRegionForPos(source.getWorldObj(), source.getCoordinates());
			if(region != null){
				if(!region.getSources().contains(source.getClimateSource())){
					region.removeSource(source.getClimateSource());
				}
			}
		}
	}

	@Override
	public void addSource(IClimateSourceProvider source) {
		if(source == null){
			return;
		}
		synchronized (regionsMutex) {
			Integer dimensionID = Integer.valueOf(source.getWorldObj().provider.getDimension());
			if(!regions.containsKey(dimensionID)){
				regions.put(dimensionID, new ArrayList<>());
			}
			IClimateRegion region = getRegionForPos(source.getWorldObj(), source.getCoordinates());
			if(region != null){
				if(!region.getSources().contains(source.getClimateSource())){
					region.addSource(source.getClimateSource());
				}
			}
		}
	}

	@Override
	public float getTemperature(World world, BlockPos pos) {
		Biome biome = world.getBiomeGenForCoords(pos);
		IClimateRegion region = getRegionForPos(world, pos);
		if(region!= null){
			IClimatePosition position = region.getPositions().get(pos);

			if(position != null){
				return position.getTemperature();
			}
		}
		return biome.getTemperature();
	}

	@Override
	public float getHumidity(World world, BlockPos pos) {
		Biome biome = world.getBiomeGenForCoords(pos);
		IClimateRegion region = getRegionForPos(world, pos);
		if(region!= null){
			IClimatePosition position = region.getPositions().get(pos);

			if(position != null){
				return position.getHumidity();
			}
		}
		return biome.getRainfall();
	}

	@Override
	public Map<Integer, List<IClimateRegion>> getRegions() {
		return regions;
	}

	@Override
	public IClimateRegion getRegionForPos(World world, BlockPos pos){
		Integer dimensionID = Integer.valueOf(world.provider.getDimension());
		if(!regions.containsKey(dimensionID)){
			this.regions.put(dimensionID, new ArrayList<>());
		}
		List<IClimateRegion> regions = this.regions.get(dimensionID);
		for(IClimateRegion region : regions){
			if(region.getPositions().keySet().contains(pos)){
				return region;
			}else if(region.getOtherPositions().contains(pos)){
				return region;
			}
		}
		return null;
	}

	@Override
	public IClimateProvider getDefaultClimate(World world, BlockPos pos) {
		return new DefaultClimateProvider(world, pos);
	}

}
