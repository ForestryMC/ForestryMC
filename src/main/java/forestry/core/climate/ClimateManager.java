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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import forestry.api.core.IClimateProvider;
import forestry.api.core.climate.IClimateManager;
import forestry.api.core.climate.IClimatePosition;
import forestry.api.core.climate.IClimateRegion;
import forestry.api.core.climate.IClimateSource;
import forestry.core.DefaultClimateProvider;
import forestry.core.multiblock.MultiblockRegistry;
import forestry.core.multiblock.MultiblockWorldRegistry;

public class ClimateManager implements IClimateManager{

	protected Map<Integer, List<IClimateRegion>> regions;
	protected Map<Integer, Map<BlockPos, IClimateSource>> sources;
	
	private final Object regionsMutex;
	
	public ClimateManager() {
		regions = new HashMap<>();
		sources = new HashMap<>();
		regionsMutex = new Object();
	}
	
	@Override
	public void addRegion(IClimateRegion region) {
		if(region == null){
			return;
		}
		List<IClimateRegion> regions = this.regions.get(Integer.valueOf(region.getWorld().provider.getDimension()));
		
		synchronized (regionsMutex) {
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
			List<IClimateRegion> regions = this.regions.get(Integer.valueOf(region.getWorld().provider.getDimension()));
			if(regions.contains(region)){
				regions.remove(region);
			}
		}
	}
	
	@Override
	public void removeSource(IClimateSource source) {
		if(sources.get(Integer.valueOf(source.getWorld().provider.getDimension())) == null){
			sources.put(Integer.valueOf(source.getWorld().provider.getDimension()), new HashMap<>());
		}
		if(sources.get(Integer.valueOf(source.getWorld().provider.getDimension())).keySet().contains(source.getPos())){
			sources.get(Integer.valueOf(source.getWorld().provider.getDimension())).remove(source.getPos(), source);
		}
	}
	
	@Override
	public void addSource(IClimateSource source) {
		if(sources.get(Integer.valueOf(source.getWorld().provider.getDimension())) == null){
			sources.put(Integer.valueOf(source.getWorld().provider.getDimension()), new HashMap<>());
		}
		if(sources.get(Integer.valueOf(source.getWorld().provider.getDimension())).get(source.getPos()) == null){
			sources.get(Integer.valueOf(source.getWorld().provider.getDimension())).put(source.getPos(), source);
		}
	}
	
	@Override
	public float getTemperature(World world, BlockPos pos) {
		Biome biome = world.getBiome(pos);
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
		Biome biome = world.getBiome(pos);
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
		List<IClimateRegion> regions = this.regions.get(Integer.valueOf(world.provider.getDimension()));
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
	public Map<Integer, Map<BlockPos, IClimateSource>> getSources() {
		return sources;
	}
	
	@Override
	public IClimateProvider getDefaultClimate(World world, BlockPos pos) {
		return new DefaultClimateProvider(world, pos);
	}

}
