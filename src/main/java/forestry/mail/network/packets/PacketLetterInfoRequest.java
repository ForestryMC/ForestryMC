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

import forestry.api.mail.EnumAddressee;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketServer;
import forestry.core.network.PacketIdServer;
import forestry.mail.gui.ContainerLetter;

public class PacketLetterInfoRequest extends ForestryPacket implements IForestryPacketServer {

	private String recipientName;
	private EnumAddressee addressType;

	public PacketLetterInfoRequest() {
	}

	public PacketLetterInfoRequest(String recipientName, EnumAddressee addressType) {
		this.recipientName = recipientName;
		this.addressType = addressType;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeUTF(recipientName);
		data.writeByte(addressType.ordinal());
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		recipientName = data.readUTF();
		addressType = EnumAddressee.values()[data.readByte()];
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayerMP player) throws IOException {
		if (!(player.openContainer instanceof ContainerLetter)) {
			return;
		}

		((ContainerLetter) player.openContainer).handleRequestLetterInfo(player, recipientName, addressType);
	}

	@Override
	public PacketIdServer getPacketId() {
		return PacketIdServer.LETTER_INFO_REQUEST;
	}
}
