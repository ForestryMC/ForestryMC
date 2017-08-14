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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.climate.IClimateContainer;
import forestry.api.climate.IClimateSourceOwner;
import forestry.api.climate.IClimateState;
import forestry.api.greenhouse.IGreenhouseBlock;
import forestry.api.greenhouse.IGreenhouseProvider;
import forestry.api.greenhouse.Position2D;
import forestry.greenhouse.multiblock.blocks.world.GreenhouseBlockManager;

public class ClimateWorldManager{
	
	private final Set<IClimateContainer> containers;
	private final Cache<BlockPos, IClimateState> stateCache;
	private final ClimateManager parent;
	private final Map<Position2D, Map<Integer, IClimateSourceOwner>> owners;

	public ClimateWorldManager(ClimateManager parent) {
		this.containers = new HashSet<>();
		this.stateCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.SECONDS).build();
		this.parent = parent;
		this.owners = new HashMap<>();
	}
	
	public IClimateState getClimateState(World world, BlockPos pos) {
		IClimateState cacheState = stateCache.getIfPresent(pos);
		if(cacheState == null){
			IClimateContainer container = getContainer(world, pos);

			if (container != null) {
				cacheState = container.getState();
			}
			if(cacheState == null){
				cacheState = parent.getBiomeState(world, pos);
			}
			stateCache.put(pos, cacheState);
		}
		return cacheState;
	}
	
	public IClimateContainer getContainer(World world, BlockPos pos) {
		IGreenhouseBlock logicBlock = GreenhouseBlockManager.getInstance().getBlock(world, pos);
		if(logicBlock != null){
			IGreenhouseProvider provider = logicBlock.getProvider();
			if(!provider.isClosed()) {
				return null;
			}
			return provider.getClimateContainer();
		}
		return null;
	}
	
	public Set<IClimateContainer> getContainers() {
		return containers;
	}
	
	public void addSource(IClimateSourceOwner owner){
		BlockPos pos = owner.getCoordinates();
		Position2D position = new Position2D(pos);
		Map<Integer, IClimateSourceOwner> positionedOwners = owners.computeIfAbsent(position, k->new HashMap<>());
		positionedOwners.put(pos.getY(), owner);
	}
	
	public void removeSource(IClimateSourceOwner owner){
		BlockPos pos = owner.getCoordinates();
		Position2D position = new Position2D(pos);
		Map<Integer, IClimateSourceOwner> positionedOwners = owners.computeIfAbsent(position, k->new HashMap<>());
		positionedOwners.remove(pos.getY());
	}

	public Collection<IClimateSourceOwner> getSources(Position2D position) {
		Map<Integer, IClimateSourceOwner> positionedOwners = owners.get(position);
		if(positionedOwners == null){
			return Collections.emptyList();
		}
		return positionedOwners.values();
	}
	
}