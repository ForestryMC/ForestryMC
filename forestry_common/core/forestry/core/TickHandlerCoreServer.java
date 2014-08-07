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
