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

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import forestry.core.proxy.Proxies;
import forestry.mail.gui.GuiMailboxInfo;
import forestry.mail.network.packets.PacketPOBoxInfoResponse;

public class EventHandlerMailAlert {
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (event.phase != Phase.END) {
			return;
		}

		if (Minecraft.getMinecraft().world != null && GuiMailboxInfo.instance.hasPOBoxInfo()) {
			GuiMailboxInfo.instance.render();
		}
	}

	@SubscribeEvent
	public void handlePlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		if (player != null) {
			MailAddress address = new MailAddress(player.getGameProfile());
			POBox pobox = PostRegistry.getOrCreatePOBox(player.world, address);
			Proxies.net.sendToPlayer(new PacketPOBoxInfoResponse(pobox.getPOBoxInfo()), player);
		}
	}
}
