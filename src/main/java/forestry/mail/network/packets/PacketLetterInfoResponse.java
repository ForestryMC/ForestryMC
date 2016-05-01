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
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import com.mojang.authlib.GameProfile;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.EnumTradeStationState;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.ITradeStationInfo;
import forestry.api.mail.PostManager;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.PacketIdClient;
import forestry.mail.TradeStationInfo;
import forestry.mail.gui.ILetterInfoReceiver;

public class PacketLetterInfoResponse extends ForestryPacket implements IForestryPacketClient {

	public EnumAddressee type;
	public ITradeStationInfo tradeInfo;
	public IMailAddress address;

	public PacketLetterInfoResponse() {
	}

	public PacketLetterInfoResponse(EnumAddressee type, ITradeStationInfo info, IMailAddress address) {
		this.type = type;
		if (type == EnumAddressee.TRADER) {
			this.tradeInfo = info;
		} else if (type == EnumAddressee.PLAYER) {
			this.address = address;
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.LETTER_INFO_RESPONSE;
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

			data.writeUTF(tradeInfo.getAddress().getName());

			data.writeLong(tradeInfo.getOwner().getId().getMostSignificantBits());
			data.writeLong(tradeInfo.getOwner().getId().getLeastSignificantBits());
			data.writeUTF(tradeInfo.getOwner().getName());

			data.writeItemStack(tradeInfo.getTradegood());
			data.writeItemStacks(tradeInfo.getRequired());

			data.writeShort(tradeInfo.getState().ordinal());
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

			this.tradeInfo = new TradeStationInfo(address, owner, tradegood, required, EnumTradeStationState.values()[data.readShort()]);
		}
	}

	@Override
	public void onPacketData(DataInputStreamForestry data, EntityPlayer player) throws IOException {
		Container container = player.openContainer;
		if (container instanceof ILetterInfoReceiver) {
			((ILetterInfoReceiver) container).handleLetterInfoUpdate(this);
		}
	}
}
