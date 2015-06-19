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
package forestry.mail.network;

import java.io.IOException;

import forestry.api.mail.EnumAddressee;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.ForestryPacket;
import forestry.core.network.PacketId;

public class PacketRequestLetterInfo extends ForestryPacket {

	private String recipientName;
	private short addressType;

	public PacketRequestLetterInfo(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketRequestLetterInfo(String recipientName, EnumAddressee addressType) {
		super(PacketId.LETTER_REQUEST_INFO);
		this.recipientName = recipientName;
		this.addressType = (short) addressType.ordinal();
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		super.writeData(data);
		data.writeUTF(recipientName);
		data.writeShort(addressType);
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {
		super.readData(data);
		recipientName = data.readUTF();
		addressType = data.readShort();
	}

	public String getRecipientName() {
		return recipientName;
	}

	public EnumAddressee getAddressType() {
		return EnumAddressee.values()[addressType];
	}
}
