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
import java.util.UUID;

import net.minecraft.item.ItemStack;

import com.mojang.authlib.GameProfile;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.PostManager;
import forestry.api.mail.TradeStationInfo;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.ForestryPacket;
import forestry.core.network.PacketId;
import forestry.mail.EnumStationState;

public class PacketLetterInfo extends ForestryPacket {

	public EnumAddressee type;
	public TradeStationInfo tradeInfo;
	public IMailAddress address;

	public PacketLetterInfo(DataInputStreamForestry data) throws IOException {
		super(data);
	}

	public PacketLetterInfo(PacketId id, EnumAddressee type, TradeStationInfo info, IMailAddress address) {
		super(id);
		this.type = type;
		if (type == EnumAddressee.TRADER) {
			this.tradeInfo = info;
		} else if (type == EnumAddressee.PLAYER) {
			this.address = address;
		}
	}

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {

		if (type == null) {
			data.writeShort(-1);
			return;
		}
		data.writeShort(0);

		data.writeUTF(type.toString());

		if (type == EnumAddressee.PLAYER) {

			if (address == null) {
				data.writeShort(-1);
				return;
			}
			data.writeShort(0);

			GameProfile profile = address.getPlayerProfile();

			data.writeLong(profile.getId().getMostSignificantBits());
			data.writeLong(profile.getId().getLeastSignificantBits());
			data.writeUTF(profile.getName());

		} else if (type == EnumAddressee.TRADER) {

			if (tradeInfo == null) {
				data.writeShort(-1);
				return;
			}

			data.writeShort(0);

			data.writeUTF(tradeInfo.address.getName());

			data.writeLong(tradeInfo.owner.getId().getMostSignificantBits());
			data.writeLong(tradeInfo.owner.getId().getLeastSignificantBits());
			data.writeUTF(tradeInfo.owner.getName());

			data.writeItemStack(tradeInfo.tradegood);
			data.writeItemStacks(tradeInfo.required);

			data.writeShort(tradeInfo.state.ordinal());
		}
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {

		if (data.readShort() < 0) {
			return;
		}

		type = EnumAddressee.fromString(data.readUTF());

		if (type == EnumAddressee.PLAYER) {
			if (data.readShort() < 0) {
				return;
			}
			GameProfile player = new GameProfile(new UUID(data.readLong(), data.readLong()), data.readUTF());
			this.address = PostManager.postRegistry.getMailAddress(player);

		} else if (type == EnumAddressee.TRADER) {
			if (data.readShort() < 0) {
				return;
			}
			IMailAddress address = PostManager.postRegistry.getMailAddress(data.readUTF());
			GameProfile owner = new GameProfile(new UUID(data.readLong(), data.readLong()), data.readUTF());
			ItemStack tradegood;
			ItemStack[] required;

			tradegood = data.readItemStack();
			required = data.readItemStacks();

			this.tradeInfo = new TradeStationInfo(address, owner, tradegood, required, EnumStationState.values()[data.readShort()]);
		}
	}

}
