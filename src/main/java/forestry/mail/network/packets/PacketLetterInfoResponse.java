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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.EnumTradeStationState;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.ITradeStationInfo;
import forestry.api.mail.PostManager;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;
import forestry.mail.TradeStationInfo;
import forestry.mail.gui.ILetterInfoReceiver;

// TODO: split this into two different packets
public class PacketLetterInfoResponse extends ForestryPacket implements IForestryPacketClient {
	public final EnumAddressee type;
	@Nullable
	public final ITradeStationInfo tradeInfo;
	@Nullable
	public final IMailAddress address;

	public PacketLetterInfoResponse(EnumAddressee type, @Nullable ITradeStationInfo info, @Nullable IMailAddress address) {
		this.type = type;
		if (type == EnumAddressee.TRADER) {
			this.tradeInfo = info;
			this.address = null;
		} else if (type == EnumAddressee.PLAYER) {
			this.tradeInfo = info;
			this.address = address;
		} else {
			throw new IllegalArgumentException("Unknown addressee type: " + type);
		}
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.LETTER_INFO_RESPONSE;
	}

	@Override
	public void writeData(PacketBufferForestry data) {
		data.writeEnum(type, EnumAddressee.values());

		if (type == EnumAddressee.PLAYER) {
			Preconditions.checkNotNull(address);
			GameProfile profile = address.getPlayerProfile();

			data.writeLong(profile.getId().getMostSignificantBits());
			data.writeLong(profile.getId().getLeastSignificantBits());
			data.writeString(profile.getName());

		} else if (type == EnumAddressee.TRADER) {
			if (tradeInfo == null) {
				data.writeBoolean(false);
			} else {
				data.writeBoolean(true);
				data.writeString(tradeInfo.getAddress().getName());

				data.writeLong(tradeInfo.getOwner().getId().getMostSignificantBits());
				data.writeLong(tradeInfo.getOwner().getId().getLeastSignificantBits());
				data.writeString(tradeInfo.getOwner().getName());

				data.writeItemStack(tradeInfo.getTradegood());
				data.writeItemStacks(tradeInfo.getRequired());

				data.writeEnum(tradeInfo.getState(), EnumTradeStationState.values());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, EntityPlayer player) throws IOException {
			Container container = player.openContainer;
			if (container instanceof ILetterInfoReceiver) {
				EnumAddressee type = data.readEnum(EnumAddressee.values());
				ITradeStationInfo tradeInfo = null;
				IMailAddress address = null;

				if (type == EnumAddressee.PLAYER) {
					GameProfile profile = new GameProfile(new UUID(data.readLong(), data.readLong()), data.readString());
					address = PostManager.postRegistry.getMailAddress(profile);
				} else if (type == EnumAddressee.TRADER) {
					if (data.readBoolean()) {
						address = PostManager.postRegistry.getMailAddress(data.readString());
						GameProfile owner = new GameProfile(new UUID(data.readLong(), data.readLong()), data.readString());

						ItemStack tradegood = data.readItemStack();
						NonNullList<ItemStack> required = data.readItemStacks();

						EnumTradeStationState state = data.readEnum(EnumTradeStationState.values());
						tradeInfo = new TradeStationInfo(address, owner, tradegood, required, state);
					} else {
						tradeInfo = null;
					}
				}
				((ILetterInfoReceiver) container).handleLetterInfoUpdate(type, address, tradeInfo);
			}
		}
	}
}
