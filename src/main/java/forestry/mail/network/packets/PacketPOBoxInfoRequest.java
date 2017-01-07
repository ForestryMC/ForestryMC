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
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketIdServer;
import forestry.core.proxy.Proxies;
import forestry.mail.POBox;
import forestry.mail.PostRegistry;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketPOBoxInfoRequest extends ForestryPacket implements IForestryPacketServer {
	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) throws IOException {
		IMailAddress address = PostManager.postRegistry.getMailAddress(player.getGameProfile());
		POBox pobox = PostRegistry.getOrCreatePOBox(player.worldObj, address);
		PacketPOBoxInfoUpdate packet = new PacketPOBoxInfoUpdate(pobox.getPOBoxInfo());
		Proxies.net.sendToPlayer(packet, player);
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.POBOX_INFO_REQUEST;
	}
}
