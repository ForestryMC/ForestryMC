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
package forestry.mail;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.IPacketHandler;
import forestry.core.network.PacketId;
import forestry.core.network.PacketString;
import forestry.core.proxy.Proxies;
import forestry.mail.gadgets.MachineTrader;
import forestry.mail.gui.ContainerCatalogue;
import forestry.mail.gui.ContainerLetter;
import forestry.mail.gui.GuiMailboxInfo;
import forestry.mail.network.PacketLetterInfo;
import forestry.mail.network.PacketPOBoxInfo;
import forestry.mail.network.PacketRequestLetterInfo;
import forestry.mail.network.PacketTraderAddress;

public class PacketHandlerMail implements IPacketHandler {

	@Override
	public boolean onPacketData(PacketId packetID, DataInputStreamForestry data, EntityPlayer player) throws IOException {

		switch (packetID) {
			case LETTER_INFO: {
				PacketLetterInfo packet = new PacketLetterInfo(data);
				onLetterInfo(packet);
				return true;
			}
			case POBOX_INFO: {
				PacketPOBoxInfo packet = new PacketPOBoxInfo(data);
				onPOBoxInfo(packet);
				return true;
			}
			case LETTER_REQUEST_INFO: {
				PacketRequestLetterInfo packet = new PacketRequestLetterInfo(data);
				onLetterRequestInfo(player, packet);
				return true;
			}
			case LETTER_TEXT: {
				PacketString packet = new PacketString(data);
				onLetterText(player, packet);
				return true;
			}
			case TRADING_ADDRESS_SET: {
				PacketTraderAddress packet = new PacketTraderAddress(data);
				handleTradeAddressSet(player, packet);
				return true;
			}
			case POBOX_INFO_REQUEST: {
				onPOBoxInfoRequest(player);
				return true;
			}
		}

		return false;
	}

	private static void onLetterInfo(PacketLetterInfo packet) {

		Container container = Proxies.common.getClientInstance().thePlayer.openContainer;
		if (container instanceof ContainerLetter) {
			((ContainerLetter) container).handleLetterInfoUpdate(packet);
		} else if (container instanceof ContainerCatalogue) {
			((ContainerCatalogue) container).handleTradeInfoUpdate(packet);
		}

	}

	private static void onPOBoxInfo(PacketPOBoxInfo packet) {
		GuiMailboxInfo.instance.setPOBoxInfo(packet.poboxInfo);
	}

	private static void handleTradeAddressSet(EntityPlayer player, PacketTraderAddress packet) {
		TileEntity tile = packet.getTarget(player.worldObj);
		if (!(tile instanceof MachineTrader)) {
			return;
		}

		String addressName = packet.getAddressName();

		((MachineTrader) tile).handleSetAddress(addressName);
	}

	private static void onLetterText(EntityPlayer player, PacketString packet) {
		if (!(player.openContainer instanceof ContainerLetter)) {
			return;
		}

		((ContainerLetter) player.openContainer).handleSetText(packet);
	}

	private static void onLetterRequestInfo(EntityPlayer player, PacketRequestLetterInfo packet) {
		if (!(player.openContainer instanceof ContainerLetter)) {
			return;
		}

		((ContainerLetter) player.openContainer).handleRequestLetterInfo(player, packet);
	}

	private static void onPOBoxInfoRequest(EntityPlayer player) {
		MailAddress address = new MailAddress(player.getGameProfile());
		POBox pobox = PostRegistry.getOrCreatePOBox(player.worldObj, address);
		if (pobox == null) {
			return;
		}

		Proxies.net.sendToPlayer(new PacketPOBoxInfo(pobox.getPOBoxInfo()), player);
	}

}
