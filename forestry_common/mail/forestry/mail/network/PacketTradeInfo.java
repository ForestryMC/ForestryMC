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
import java.util.UUID;

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

		data.writeLong(tradeInfo.moniker.getId().getMostSignificantBits());
		data.writeLong(tradeInfo.moniker.getId().getLeastSignificantBits());
		data.writeUTF(tradeInfo.moniker.getName());

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

		GameProfile moniker = new GameProfile(new UUID(data.readLong(), data.readLong()), data.readUTF());
		GameProfile owner = new GameProfile(new UUID(data.readLong(), data.readLong()), data.readUTF());
		ItemStack tradegood;
		ItemStack[] required;

		tradegood = readItemStack(data);
		required = new ItemStack[data.readShort()];
		for (int i = 0; i < required.length; i++)
			required[i] = readItemStack(data);

		this.tradeInfo = new TradeStationInfo(moniker, owner, tradegood, required, EnumStationState.values()[data.readShort()]);
	}

}
