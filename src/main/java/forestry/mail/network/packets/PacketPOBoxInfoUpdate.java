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

import net.minecraft.entity.player.EntityPlayer;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.mail.POBoxInfo;
import forestry.mail.gui.GuiMailboxInfo;

public class PacketPOBoxInfoUpdate extends ForestryPacket implements IForestryPacketClient {

	public POBoxInfo poboxInfo;

	public PacketPOBoxInfoUpdate() {
	}

	public PacketPOBoxInfoUpdate(POBoxInfo info) {
		this.poboxInfo = info;
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.POBOX_INFO_RESPONSE;
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
	public void readData(DataInputStreamForestry data) throws IOException {
		short isNotNull = data.readShort();
		if (isNotNull < 0) {
			return;
		}

		this.poboxInfo = new POBoxInfo(data.readInt(), data.readInt());
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		GuiMailboxInfo.instance.setPOBoxInfo(poboxInfo);
	}

}
