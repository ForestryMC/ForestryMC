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

import forestry.api.mail.MailAddress;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import forestry.core.interfaces.IPacketHandler;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.mail.gui.ContainerCatalogue;
import forestry.mail.gui.ContainerLetter;
import forestry.mail.gui.ContainerTradeName;
import forestry.mail.network.PacketPOBoxInfo;
import forestry.mail.network.PacketTradeInfo;
import forestry.plugins.PluginMail;

public class PacketHandlerMail implements IPacketHandler {

	@Override
	public void onPacketData(int packetID, DataInputStream data, EntityPlayer player) {

		try {

			PacketUpdate packet;
			switch (packetID) {
			case PacketIds.TRADING_INFO:
				PacketTradeInfo packetT = new PacketTradeInfo();
				packetT.readData(data);
				onTradeInfo(packetT);
				break;
			case PacketIds.POBOX_INFO:
				PacketPOBoxInfo packetP = new PacketPOBoxInfo();
				packetP.readData(data);
				onPOBoxInfo(packetP);
				break;
			case PacketIds.LETTER_RECIPIENT:
				packet = new PacketUpdate();
				packet.readData(data);
				onLetterRecipient(player, packet);
				break;
			case PacketIds.LETTER_TEXT:
				packet = new PacketUpdate();
				packet.readData(data);
				onLetterText(player, packet);
				break;
			case PacketIds.TRADING_MONIKER_SET:
				packet = new PacketUpdate();
				packet.readData(data);
				onMonikerSet(player, packet);
				break;
			case PacketIds.POBOX_INFO_REQUEST:
				onPOBoxInfoRequest(player);
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void onTradeInfo(PacketTradeInfo packet) {

		Container container = Proxies.common.getClientInstance().thePlayer.openContainer;
		if (container instanceof ContainerLetter) {
			((ContainerLetter) container).handleTradeInfoUpdate(packet);
		} else if(container instanceof ContainerCatalogue) {
			((ContainerCatalogue) container).handleTradeInfoUpdate(packet);
		}

	}

	private void onPOBoxInfo(PacketPOBoxInfo packet) {
		MailAddress address = new MailAddress(Proxies.common.getClientInstance().thePlayer.getGameProfile());
		PluginMail.proxy.setPOBoxInfo(Proxies.common.getRenderWorld(), address, packet.poboxInfo);
	}

	private void onMonikerSet(EntityPlayer player, PacketUpdate packet) {
		if (!(player.openContainer instanceof ContainerTradeName))
			return;

		((ContainerTradeName) player.openContainer).handleSetMoniker(packet);
	}

	private void onLetterText(EntityPlayer player, PacketUpdate packet) {
		if (!(player.openContainer instanceof ContainerLetter))
			return;

		((ContainerLetter) player.openContainer).handleSetText(packet);
	}

	private void onLetterRecipient(EntityPlayer player, PacketUpdate packet) {
		if (!(player.openContainer instanceof ContainerLetter))
			return;

		((ContainerLetter) player.openContainer).handleSetRecipient(player, packet);
	}

	private void onPOBoxInfoRequest(EntityPlayer player) {
		MailAddress address = new MailAddress(player.getGameProfile());
		POBox pobox = PostRegistry.getPOBox(player.worldObj, address);
		if (pobox == null)
			return;

		Proxies.net.sendToPlayer(new PacketPOBoxInfo(PacketIds.POBOX_INFO, pobox.getPOBoxInfo()), player);
	}

}
