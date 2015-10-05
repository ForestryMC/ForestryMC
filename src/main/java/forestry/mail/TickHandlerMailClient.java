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
package forestry.mail;

import forestry.core.network.ForestryPacket;
import forestry.core.network.PacketId;
import forestry.core.proxy.Proxies;
import forestry.mail.gui.GuiMailboxInfo;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class TickHandlerMailClient {
	public TickHandlerMailClient() {
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event) {
		if (event.phase != Phase.END) {
			return;
		}

		if (GuiMailboxInfo.instance.hasPOBoxInfo()) {
			GuiMailboxInfo.instance.render(0, 0);
		} else {
			Proxies.net.sendToServer(new ForestryPacket(PacketId.POBOX_INFO_REQUEST));
		}
	}
}
