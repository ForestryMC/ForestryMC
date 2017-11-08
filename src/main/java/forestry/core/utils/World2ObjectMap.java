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
package forestry.core.utils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.world.World;

import net.minecraftforge.event.world.WorldEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This is a implementation of a map that works with worlds.
 * A world is automatically removed from this map if it unloads and added if it loads.
 */
public final class World2ObjectMap<O> {
	private static final Set<World2ObjectMap> MAPS = new HashSet<>();
	
	private final Map<Integer, O> delegate;
	private final Factory factory;
	@Nullable
	private final Listener listener;
	
	public World2ObjectMap(Factory factory) {
		this(factory, null);
	}
	
	public World2ObjectMap(Factory factory, @Nullable Listener listener) {
		this.delegate = new HashMap<>();
		MAPS.add(this);
		this.factory = factory;
		this.listener = listener;
	}
	
	@Nullable
	public O get(@Nullable World world){
		if(world == null){
			return null;
		}
		return delegate.get(getIndex(world));
	}
	
	public Collection<O> values(){
		return delegate.values();
	}
	
	private int getIndex(World world){
		return Integer.hashCode(world.provider.getDimension()) + Boolean.hashCode(world.isRemote);
	}
	
	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event) {
		World world = event.getWorld();
		for(World2ObjectMap map : MAPS){
			map.delegate.remove(map.getIndex(world));
			if(map.listener != null) {
				map.listener.onWorldStateChange(world, false, event);
			}
		}
	}
	
	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		World world = event.getWorld();
		for(World2ObjectMap map : MAPS){
			map.delegate.put(map.getIndex(world), map.factory.createValue(world));
			if(map.listener != null) {
				map.listener.onWorldStateChange(world, false, event);
			}
		}
	}
	
	public interface Factory<O>{
		O createValue(World world);
	}
	
	public interface Listener{
		/**
		 * @param event A WorldEvent.Unload or WorldEvent.Load event
		 */
		void onWorldStateChange(World world, boolean load, WorldEvent event);
	}
}
