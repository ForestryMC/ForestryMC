/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail;

import net.minecraft.world.World;

import forestry.core.interfaces.ISaveEventHandler;
import forestry.plugins.PluginMail;

public class SaveEventHandlerMail implements ISaveEventHandler {

	@Override
	public void onWorldLoad(World world) {
		PostRegistry.cachedPostOffice = null;
		PostRegistry.cachedPOBoxes.clear();
		PostRegistry.cachedTradeStations.clear();
		PluginMail.proxy.clearMailboxInfo();
	}

	@Override
	public void onWorldSave(World world) {
	}

	@Override
	public void onWorldUnload(World world) {
	}

}
