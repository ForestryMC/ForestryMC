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

import net.minecraft.entity.player.EntityPlayerMP;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketIdServer;
import forestry.core.proxy.Proxies;
import forestry.mail.MailAddress;
import forestry.mail.POBox;
import forestry.mail.PostRegistry;

public class PacketPOBoxInfoRequest extends ForestryPacket implements IForestryPacketServer {

	public PacketPOBoxInfoRequest() {
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.POBOX_INFO_REQUEST;
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) throws IOException {
		MailAddress address = new MailAddress(player.getGameProfile());
		POBox pobox = PostRegistry.getOrCreatePOBox(player.worldObj, address);
		if (pobox != null) {
			Proxies.net.sendToPlayer(new PacketPOBoxInfoResponse(pobox.getPOBoxInfo()), player);
		}
	}
}
