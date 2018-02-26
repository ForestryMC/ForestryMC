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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.climate.IClimateState;
import forestry.greenhouse.api.greenhouse.GreenhouseManager;
import forestry.greenhouse.api.greenhouse.Position2D;
import forestry.greenhouse.api.climate.IClimateContainer;
import forestry.greenhouse.api.climate.IGreenhouseClimateManager;
import forestry.greenhouse.api.climate.IClimateSourceOwner;

public class ClimateWorldManager {

	private final Cache<BlockPos, IClimateState> stateCache;
	private final ClimateManager parent;
	private final Map<Position2D, Map<Integer, IClimateSourceOwner>> owners;

	public ClimateWorldManager(ClimateManager parent) {
		this.stateCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.SECONDS).build();
		this.parent = parent;
		this.owners = new HashMap<>();
	}

	public IClimateState getClimateState(World world, BlockPos pos) {
		IClimateState cacheState = stateCache.getIfPresent(pos);
		if (cacheState == null) {
			IGreenhouseClimateManager climateSourceManager = GreenhouseManager.climateManager;
			if (climateSourceManager != null) {
				IClimateContainer container = climateSourceManager.getContainer(world, pos);

				if (container != null) {
					cacheState = container.getState();
				}
			}
			if (cacheState == null) {
				cacheState = parent.getBiomeState(world, pos);
			}
			stateCache.put(pos, cacheState);
		}
		return cacheState;
	}

}