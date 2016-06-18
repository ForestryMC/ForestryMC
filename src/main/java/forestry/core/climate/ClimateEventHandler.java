package forestry.core.climate;

import java.util.HashMap;
import java.util.Map;

import forestry.api.core.ForestryAPI;
import forestry.api.core.climate.IClimateRegion;
import forestry.api.core.climate.IClimateSource;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClimateEventHandler {

	Map<Integer, Integer> serverTicks;
	
	public ClimateEventHandler() {
		serverTicks = new HashMap<>();
	}
	
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		Integer dim = Integer.valueOf(event.world.provider.getDimension());
		if (event.phase == TickEvent.Phase.END) {
			if(!serverTicks.containsKey(dim)){
				serverTicks.put(dim, 1);
			}
			int ticks = serverTicks.get(dim);
			if(ticks % 20 == 0){
				if(ForestryAPI.climateManager.getRegions() != null && ForestryAPI.climateManager.getRegions().get(dim) != null){
					for(IClimateRegion region : ForestryAPI.climateManager.getRegions().get(dim)){
						region.updateClimate();
					}
				}
			}
			if(ForestryAPI.climateManager.getSources() != null && ForestryAPI.climateManager.getSources().get(dim) != null){
				for(IClimateSource source : ForestryAPI.climateManager.getSources().get(dim).values()){
					source.changeClimate(ticks, ForestryAPI.climateManager.getRegionForPos(event.world, source.getPos()));
				}
			}
			serverTicks.put(dim, ticks+1);
		}
	}
	
}
