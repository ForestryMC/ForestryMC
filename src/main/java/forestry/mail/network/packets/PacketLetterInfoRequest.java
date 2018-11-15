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

import net.minecraft.entity.player.EntityPlayerMP;

import forestry.api.mail.EnumAddressee;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketHandlerServer;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdServer;
import forestry.mail.gui.ContainerLetter;

public class PacketLetterInfoRequest extends ForestryPacket implements IForestryPacketServer {
	private final String recipientName;
	private final EnumAddressee addressType;

	public PacketLetterInfoRequest(String recipientName, EnumAddressee addressType) {
		this.recipientName = recipientName;
		this.addressType = addressType;
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.LETTER_INFO_REQUEST;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeString(recipientName);
		data.writeEnum(addressType, EnumAddressee.values());
	}

	public static class Handler implements IForestryPacketHandlerServer {

		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayerMP player) {
			String recipientName = data.readString();
			EnumAddressee addressType = data.readEnum(EnumAddressee.values());

			if (player.openContainer instanceof ContainerLetter) {
				ContainerLetter containerLetter = (ContainerLetter) player.openContainer;
				containerLetter.handleRequestLetterInfo(player, recipientName, addressType);
			}
		}
	}
}
