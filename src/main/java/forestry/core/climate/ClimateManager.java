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
import forestry.api.core.climate.IClimateManager;
import forestry.api.core.climate.IClimatePosition;
import forestry.api.core.climate.IClimateRegion;

public class ClimateManager implements IClimateManager{

	protected Map<Integer, List<IClimateRegion>> regions;
	
	public ClimateManager() {
		regions = new HashMap<>();
	}
	
	@Override
	public void addRegion(IClimateRegion region) {
		if(region == null){
			return;
		}
		List<IClimateRegion> regions = getOrCreateRegions(region.getWorld());
		List<BlockPos> positions = (List<BlockPos>) region.getPositions().keySet();
		for(IClimateRegion otherRegion : regions){
			for(BlockPos pos : otherRegion.getPositions().keySet()){
				if(positions.contains(pos)){
					return;
				}
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

}
