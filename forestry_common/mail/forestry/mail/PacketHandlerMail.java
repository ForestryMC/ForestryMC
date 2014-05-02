/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail;

import java.io.DataInputStream;

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
		PluginMail.proxy.setPOBoxInfo(Proxies.common.getRenderWorld(), Proxies.common.getClientInstance().thePlayer.getGameProfile().getId(), packet.poboxInfo);
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
		POBox pobox = PostRegistry.getPOBox(player.worldObj, player.getGameProfile().getId());
		if (pobox == null)
			return;

		Proxies.net.sendToPlayer(new PacketPOBoxInfo(PacketIds.POBOX_INFO, pobox.getPOBoxInfo()), player);
	}

}
