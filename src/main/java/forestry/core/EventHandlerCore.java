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
package forestry.core;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.errors.ErrorStateRegistry;
import forestry.core.render.TextureManager;
import forestry.plugins.PluginManager;

public class EventHandlerCore {

	public EventHandlerCore() {
	}

	@SubscribeEvent
	public void handleItemPickup(EntityItemPickupEvent event) {

		if (event.isCanceled()) {
			return;
		}

		for (IPickupHandler handler : PluginManager.pickupHandlers) {
			if (handler.onItemPickup(event.entityPlayer, event.item)) {
				event.setResult(Result.ALLOW);
				return;
			}
		}
	}

	@SubscribeEvent
	public void handlePlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		if (player != null) {
			Collection<ISpeciesRoot> speciesRoots = AlleleManager.alleleRegistry.getSpeciesRoot().values();
			for (ISpeciesRoot speciesRoot : speciesRoots) {
				IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.getEntityWorld(), player.getGameProfile());
				breedingTracker.synchToPlayer(player);
			}
		}
	}

	@SubscribeEvent
	public void handleWorldLoad(WorldEvent.Load event) {
		for (ISaveEventHandler handler : PluginManager.saveEventHandlers) {
			handler.onWorldLoad(event.world);
		}
	}

	@SubscribeEvent
	public void handleWorldSave(WorldEvent.Save event) {
		for (ISaveEventHandler handler : PluginManager.saveEventHandlers) {
			handler.onWorldSave(event.world);
		}
	}

	@SubscribeEvent
	public void handleWorldUnload(WorldEvent.Unload event) {
		for (ISaveEventHandler handler : PluginManager.saveEventHandlers) {
			handler.onWorldUnload(event.world);
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleTextureRemap(TextureStitchEvent.Pre event) {
		if (event.map.getTextureType() == 1) {
			ErrorStateRegistry.initIcons(event.map);
			TextureManager.initDefaultIcons(event.map);
		}
	}

}
