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

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

import forestry.core.proxy.Proxies;
import forestry.mail.gui.GuiMailboxInfo;
import forestry.mail.network.PacketPOBoxInfoRequest;

public class TickHandlerMailClient {
	private static final int THROTTLE_TIME_MS = 10000;
	private long lastInfoRequestTime;

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (event.phase != Phase.END) {
			return;
		}

		if (GuiMailboxInfo.instance.hasPOBoxInfo()) {
			GuiMailboxInfo.instance.render(0, 0);
		} else {
			long time = System.currentTimeMillis();
			if (time - lastInfoRequestTime > THROTTLE_TIME_MS) {
				Proxies.net.sendToServer(new PacketPOBoxInfoRequest());
				lastInfoRequestTime = time;
			}
		}
	}
}
