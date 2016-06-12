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
		int dim = event.world.provider.getDimension();
		if (event.phase == TickEvent.Phase.END) {
			if(!serverTicks.containsKey(Integer.valueOf(dim))){
				serverTicks.put(Integer.valueOf(dim), 1);
			}
			int ticks = serverTicks.get(Integer.valueOf(dim));
			if(ticks % 20 == 0){
				for(IClimateRegion region : ForestryAPI.climateManager.getRegions().get(Integer.valueOf(dim))){
					region.updateClimate();
				}
			}
			for(IClimateSource source : ForestryAPI.climateManager.getSources().get(Integer.valueOf(dim))){
				source.changeClimate(ticks, ForestryAPI.climateManager.getRegionForPos(event.world, source.getPos()));
			}
			serverTicks.put(Integer.valueOf(dim), ticks+1);
		}
	}
	
}
