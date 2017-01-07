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
package forestry.mail.network.packets;

import java.io.IOException;

import forestry.api.mail.IMailAddress;
import forestry.api.mail.PostManager;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;
import forestry.core.proxy.Proxies;
import forestry.mail.POBox;
import forestry.mail.PostRegistry;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketPOBoxInfoRequest extends ForestryPacket implements IForestryPacketServer {

	public PacketPOBoxInfoRequest() {
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.POBOX_INFO_REQUEST;
	}

	@Override
	protected void writeData(PacketBufferForestry data) throws IOException {
		// no data, just need to know which player is requesting information
	}

	public static class Handler implements IForestryPacketHandlerServer {

		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) throws IOException {
			IMailAddress address = PostManager.postRegistry.getMailAddress(player.getGameProfile());
			POBox pobox = PostRegistry.getOrCreatePOBox(player.world, address);
			PacketPOBoxInfoResponse packet = new PacketPOBoxInfoResponse(pobox.getPOBoxInfo());
			Proxies.net.sendToPlayer(packet, player);
		}
	}
}
