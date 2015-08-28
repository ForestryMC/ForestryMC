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

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.ForestryPacket;
import forestry.core.network.PacketId;
import forestry.mail.POBoxInfo;

public class PacketPOBoxInfo extends ForestryPacket {

	public POBoxInfo poboxInfo;

	public PacketPOBoxInfo(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketPOBoxInfo(POBoxInfo info) {
		super(PacketId.POBOX_INFO);
		this.poboxInfo = info;
	}

	@Override
	protected void writeData(DataOutputStreamForestry data) throws IOException {
		if (poboxInfo == null) {
			data.writeShort(-1);
			return;
		}

		data.writeShort(0);
		data.writeInt(poboxInfo.playerLetters);
		data.writeInt(poboxInfo.tradeLetters);
	}

	@Override
	protected void readData(DataInputStreamForestry data) throws IOException {

		short isNotNull = data.readShort();
		if (isNotNull < 0) {
			return;
		}

		this.poboxInfo = new POBoxInfo(data.readInt(), data.readInt());
	}

}
