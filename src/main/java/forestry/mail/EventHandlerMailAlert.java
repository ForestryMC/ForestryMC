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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.mail.IMailAddress;
import forestry.api.mail.PostManager;
import forestry.core.utils.NetworkUtil;
import forestry.mail.gui.GuiMailboxInfo;
import forestry.mail.network.packets.PacketPOBoxInfoResponse;

public class EventHandlerMailAlert {
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (event.phase == Phase.END &&
			Minecraft.getMinecraft().world != null &&
			GuiMailboxInfo.instance.hasPOBoxInfo()) {
			GuiMailboxInfo.instance.render();
		}
	}

	@SubscribeEvent
	public void handlePlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;

		IMailAddress address = PostManager.postRegistry.getMailAddress(player.getGameProfile());
		POBox pobox = PostRegistry.getOrCreatePOBox(player.world, address);
		PacketPOBoxInfoResponse packet = new PacketPOBoxInfoResponse(pobox.getPOBoxInfo());
		NetworkUtil.sendToPlayer(packet, player);
	}
}
