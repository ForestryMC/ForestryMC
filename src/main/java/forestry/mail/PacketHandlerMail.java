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

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import forestry.core.interfaces.IPacketHandler;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.mail.gui.ContainerCatalogue;
import forestry.mail.gui.ContainerLetter;
import forestry.mail.gui.ContainerTradeName;
import forestry.mail.network.PacketLetterInfo;
import forestry.mail.network.PacketPOBoxInfo;
import forestry.plugins.PluginMail;

public class PacketHandlerMail implements IPacketHandler {

	@Override
	public boolean onPacketData(int packetID, DataInputStream data, EntityPlayer player) throws IOException {

		PacketUpdate packet;
		switch (packetID) {
			case PacketIds.LETTER_INFO:
				PacketLetterInfo packetT = new PacketLetterInfo();
				packetT.readData(data);
				onLetterInfo(packetT);
				return true;
			case PacketIds.POBOX_INFO:
				PacketPOBoxInfo packetP = new PacketPOBoxInfo();
				packetP.readData(data);
				onPOBoxInfo(packetP);
				return true;
			case PacketIds.LETTER_RECIPIENT:
				packet = new PacketUpdate();
				packet.readData(data);
				onLetterRecipient(player, packet);
				return true;
			case PacketIds.LETTER_TEXT:
				packet = new PacketUpdate();
				packet.readData(data);
				onLetterText(player, packet);
				return true;
			case PacketIds.TRADING_ADDRESS_SET:
				packet = new PacketUpdate();
				packet.readData(data);
				onAddressSet(player, packet);
				return true;
			case PacketIds.POBOX_INFO_REQUEST:
				onPOBoxInfoRequest(player);
				return true;
		}

		return false;
	}

	private void onLetterInfo(PacketLetterInfo packet) {

		Container container = Proxies.common.getClientInstance().thePlayer.openContainer;
		if (container instanceof ContainerLetter) {
			((ContainerLetter) container).handleLetterInfoUpdate(packet);
		} else if (container instanceof ContainerCatalogue) {
			((ContainerCatalogue) container).handleTradeInfoUpdate(packet);
		}

	}

	private void onPOBoxInfo(PacketPOBoxInfo packet) {
		MailAddress address = new MailAddress(Proxies.common.getClientInstance().thePlayer.getGameProfile());
		PluginMail.proxy.setPOBoxInfo(Proxies.common.getRenderWorld(), address, packet.poboxInfo);
	}

	private void onAddressSet(EntityPlayer player, PacketUpdate packet) {
		if (!(player.openContainer instanceof ContainerTradeName)) {
			return;
		}

		((ContainerTradeName) player.openContainer).handleSetAddress(packet);
	}

	private void onLetterText(EntityPlayer player, PacketUpdate packet) {
		if (!(player.openContainer instanceof ContainerLetter)) {
			return;
		}

		((ContainerLetter) player.openContainer).handleSetText(packet);
	}

	private void onLetterRecipient(EntityPlayer player, PacketUpdate packet) {
		if (!(player.openContainer instanceof ContainerLetter)) {
			return;
		}

		((ContainerLetter) player.openContainer).handleSetRecipient(player, packet);
	}

	private void onPOBoxInfoRequest(EntityPlayer player) {
		MailAddress address = new MailAddress(player.getGameProfile());
		POBox pobox = PostRegistry.getPOBox(player.worldObj, address);
		if (pobox == null) {
			return;
		}

		Proxies.net.sendToPlayer(new PacketPOBoxInfo(PacketIds.POBOX_INFO, pobox.getPOBoxInfo()), player);
	}

}
