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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import forestry.api.core.climate.IClimateManager;
import forestry.api.core.climate.IClimatePosition;
import forestry.api.core.climate.IClimateRegion;
import forestry.api.core.climate.IClimateSource;

public class ClimateManager implements IClimateManager{

	protected Map<Integer, List<IClimateRegion>> regions;
	protected Map<Integer, Map<BlockPos, IClimateSource>> sources;
	
	public ClimateManager() {
		regions = new HashMap<>();
		sources = new HashMap<>();
	}
	
	@Override
	public void addRegion(IClimateRegion region) {
		if(region == null){
			return;
		}
		List<IClimateRegion> regions = getOrCreateRegions(region.getWorld());
		Set<BlockPos> positions = region.getPositions().keySet();
		for(BlockPos pos : region.getPositions().keySet()){
			if(getRegionForPos(region.getWorld(), pos) != null){
				return;
			}
		}
		if(!regions.contains(region)){
			regions.add(region);
		}
	}
	
	@Override
	public void removeRegion(IClimateRegion region) {
		if(region == null){
			return;
		}
		List<IClimateRegion> regions = getOrCreateRegions(region.getWorld());
		if(regions.contains(region)){
			regions.remove(region);
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
		if(!sources.get(Integer.valueOf(source.getWorld().provider.getDimension())).keySet().contains(source.getPos())){
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
		for(IClimateRegion region : getOrCreateRegions(world)){
			if(region.getPositions().keySet().contains(pos)){
				return region;
			}else if(region.getOtherPositions().contains(pos)){
				return region;
			}
		}
		return null;
	}
	
	public List<IClimateRegion> getOrCreateRegions(World world){
		List<IClimateRegion> regions = this.regions.get(Integer.valueOf(world.provider.getDimension()));
		if(regions == null){
			regions = new ArrayList<>();
			this.regions.put(Integer.valueOf(world.provider.getDimension()), regions);
		}
		return regions;
	}
	
	@Override
	public Map<Integer, Map<BlockPos, IClimateSource>> getSources() {
		return sources;
	}

}
