/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

import forestry.core.interfaces.IResupplyHandler;
import forestry.plugins.PluginManager;

public class TickHandlerCoreServer {
	public TickHandlerCoreServer() {
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event) {
		if (event.phase != Phase.END) return;

		for (Object obj : event.world.playerEntities) {
			EntityPlayer player = (EntityPlayer) obj;
			for (IResupplyHandler handler : PluginManager.resupplyHandlers)
				handler.resupply(player);
		}

	}
}
