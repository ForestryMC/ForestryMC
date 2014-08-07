/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.mail.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import forestry.core.network.ForestryPacket;
import forestry.mail.POBoxInfo;

public class PacketPOBoxInfo extends ForestryPacket {

	public POBoxInfo poboxInfo;

	public PacketPOBoxInfo() {
	}

	public PacketPOBoxInfo(int id, POBoxInfo info) {
		super(id);
		this.poboxInfo = info;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		if (poboxInfo == null) {
			data.writeShort(-1);
			return;
		}

		data.writeShort(0);
		data.writeInt(poboxInfo.playerLetters);
		data.writeInt(poboxInfo.tradeLetters);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {

		short isNotNull = data.readShort();
		if (isNotNull < 0)
			return;

		this.poboxInfo = new POBoxInfo(data.readInt(), data.readInt());
	}

}
