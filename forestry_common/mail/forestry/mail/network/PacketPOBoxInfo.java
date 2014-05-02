/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
