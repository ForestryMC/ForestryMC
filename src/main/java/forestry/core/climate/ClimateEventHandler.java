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
import java.util.List;
import java.util.Map;

import forestry.api.climate.IClimateRegion;
import forestry.api.core.ForestryAPI;

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
			Map<Integer,  List<IClimateRegion>> regions = ForestryAPI.climateManager.getRegions();
			if(regions != null && regions.containsKey(dim)){
				for(IClimateRegion region : regions.get(dim)){
					region.updateClimate(ticks);
				}
			}
			serverTicks.put(dim, ticks+1);
		}
	}

}
