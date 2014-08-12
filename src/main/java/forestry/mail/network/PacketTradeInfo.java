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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import forestry.api.mail.MailAddress;
import net.minecraft.item.ItemStack;

import com.mojang.authlib.GameProfile;

import forestry.api.mail.TradeStationInfo;
import forestry.core.network.ForestryPacket;
import forestry.mail.EnumStationState;

public class PacketTradeInfo extends ForestryPacket {

	public TradeStationInfo tradeInfo;

	public PacketTradeInfo() {
	}

	public PacketTradeInfo(int id, TradeStationInfo info) {
		super(id);
		this.tradeInfo = info;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {

		if (tradeInfo == null) {
			data.writeShort(-1);
			return;
		}

		data.writeShort(0);

		String moniker = tradeInfo.address.getIdentifierName();
		data.writeUTF(moniker);

		data.writeLong(tradeInfo.owner.getId().getMostSignificantBits());
		data.writeLong(tradeInfo.owner.getId().getLeastSignificantBits());
		data.writeUTF(tradeInfo.owner.getName());

		writeItemStack(tradeInfo.tradegood, data);
		data.writeShort(tradeInfo.required.length);
		for (int i = 0; i < tradeInfo.required.length; i++)
			writeItemStack(tradeInfo.required[i], data);
		data.writeShort(tradeInfo.state.ordinal());
	}

	@Override
	public void readData(DataInputStream data) throws IOException {

		short isNotNull = data.readShort();
		if (isNotNull < 0)
			return;

		String moniker = data.readUTF();
		GameProfile owner = new GameProfile(new UUID(data.readLong(), data.readLong()), data.readUTF());
		ItemStack tradegood;
		ItemStack[] required;

		tradegood = readItemStack(data);
		required = new ItemStack[data.readShort()];
		for (int i = 0; i < required.length; i++)
			required[i] = readItemStack(data);

		MailAddress address = new MailAddress(moniker);
		this.tradeInfo = new TradeStationInfo(address, owner, tradegood, required, EnumStationState.values()[data.readShort()]);
	}

}
