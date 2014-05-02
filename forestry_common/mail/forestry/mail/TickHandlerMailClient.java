/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

import forestry.mail.gui.GuiMailboxInfo;
import forestry.plugins.PluginMail;

public class TickHandlerMailClient {
	public TickHandlerMailClient() {
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		if (event.phase != Phase.END) return;

		if (GuiMailboxInfo.instance != null)
			GuiMailboxInfo.instance.render(0, 0);
		else
			PluginMail.proxy.resetMailboxInfo();
	}
}
