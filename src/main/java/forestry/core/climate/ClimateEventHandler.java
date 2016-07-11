package forestry.core.climate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import forestry.api.core.ForestryAPI;
import forestry.api.core.climate.IClimateManager;
import forestry.api.core.climate.IClimateRegion;
import forestry.api.core.climate.IClimateSource;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClimateEventHandler {

	Map<Integer, Integer> serverTicks;
	
	public ClimateEventHandler() {
		serverTicks = new HashMap<>();
	}
	
	@SubscribeEvent
	public void onLoadWorld(WorldEvent.Load event){
		IClimateManager manager = ForestryAPI.climateManager;
		Integer dimonsionID = Integer.valueOf(event.getWorld().provider.getDimension());
		Map<Integer, List<IClimateRegion>> regions = manager.getRegions();
		Map<Integer, Map<BlockPos, IClimateSource>> sources = manager.getSources();
		if(regions.get(dimonsionID) == null){
			regions.put(dimonsionID, new ArrayList<>());
		}
		if(sources.get(dimonsionID) == null){
			sources.put(dimonsionID, new HashMap<>());
		}
	}
	
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		Integer dim = Integer.valueOf(event.world.provider.getDimension());
		if (event.phase == TickEvent.Phase.END) {
			if(!serverTicks.containsKey(dim)){
				serverTicks.put(dim, 1);
			}
			int ticks = serverTicks.get(dim);
			Map<Integer,  List<IClimateRegion>> regions = ForestryAPI.climateManager.getRegions();
			if(regions != null && regions.containsKey(dim)){
				for(IClimateRegion region : regions.get(dim)){
					if(ticks % region.getTicksPerUpdate() == 0){
						region.updateClimate();
					}
				}
			}
			Map<Integer, Map<BlockPos, IClimateSource>> sources = ForestryAPI.climateManager.getSources();
			if(sources != null && sources.containsKey(dim)){
				for(IClimateSource source : sources.get(dim).values()){
					IClimateRegion region = ForestryAPI.climateManager.getRegionForPos(event.world, source.getPos());
					if(region != null){
						if(ticks % source.getTicksForChange(region) == 0){
							source.changeClimate(ticks, region);
						}
					}
				}
			}
			serverTicks.put(dim, ticks+1);
		}
	}
	
}
