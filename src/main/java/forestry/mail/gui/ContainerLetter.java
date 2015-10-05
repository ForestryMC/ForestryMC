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
package forestry.mail.gui;

import java.util.Iterator;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.ILetter;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IPostalCarrier;
import forestry.api.mail.ITradeStation;
import forestry.api.mail.PostManager;
import forestry.api.mail.TradeStationInfo;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.network.PacketId;
import forestry.core.network.PacketString;
import forestry.core.proxy.Proxies;
import forestry.mail.Letter;
import forestry.mail.items.ItemLetter.LetterInventory;
import forestry.mail.network.PacketLetterInfo;
import forestry.mail.network.PacketRequestLetterInfo;

public class ContainerLetter extends ContainerItemInventory<LetterInventory> {

	private EnumAddressee carrierType = EnumAddressee.PLAYER;
	private TradeStationInfo tradeInfo = null;

	public ContainerLetter(EntityPlayer player, LetterInventory inventory) {
		super(inventory, player.inventory, 17, 145);

		// Init slots

		// Stamps
		for (int i = 0; i < 4; i++) {
			addSlotToContainer(new SlotFiltered(inventory, Letter.SLOT_POSTAGE_1 + i, 150, 14 + i * 19).setStackLimit(1));
		}

		// Attachments
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new SlotFiltered(inventory, Letter.SLOT_ATTACHMENT_1 + j + i * 9, 17 + j * 18, 98 + i * 18));
			}
		}

		// Rip open delivered mails
		if (Proxies.common.isSimulating(player.worldObj)) {
			if (inventory.getLetter().isProcessed()) {
				inventory.onLetterOpened();
			}
		}

		// Set recipient type
		ILetter letter = inventory.getLetter();
		if (letter != null) {
			IMailAddress[] recipients = letter.getRecipients();
			if (recipients != null && recipients.length > 0) {
				this.carrierType = recipients[0].getType();
			}
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {

		if (Proxies.common.isSimulating(entityplayer.worldObj)) {
			ILetter letter = inventory.getLetter();
			if (!letter.isProcessed()) {
				IMailAddress sender = PostManager.postRegistry.getMailAddress(entityplayer.getGameProfile());
				letter.setSender(sender);
			}
		}

		inventory.onContainerClosed();

		super.onContainerClosed(entityplayer);
	}

	public ILetter getLetter() {
		return inventory.getLetter();
	}

	public void setCarrierType(EnumAddressee type) {
		this.carrierType = type;
	}

	public EnumAddressee getCarrierType() {
		return this.carrierType;
	}

	public void advanceCarrierType() {
		Iterator<IPostalCarrier> it = PostManager.postRegistry.getRegisteredCarriers().values().iterator();
		while (it.hasNext()) {
			if (it.next().getType().equals(carrierType)) {
				break;
			}
		}

		IPostalCarrier postal;
		if (it.hasNext()) {
			postal = it.next();
		} else {
			postal = PostManager.postRegistry.getRegisteredCarriers().values().iterator().next();
		}

		setCarrierType(postal.getType());
	}

	public void handleRequestLetterInfo(EntityPlayer player, PacketRequestLetterInfo packet) {
		String recipientName = packet.getRecipientName();
		EnumAddressee type = packet.getAddressType();

		IMailAddress recipient = getRecipient(recipientName, type);

		getLetter().setRecipient(recipient);
		
		// Update the trading info
		if (recipient == null || recipient.isTrader()) {
			updateTradeInfo(player.worldObj, recipient);
		}
		
		// Update info on client
		Proxies.net.sendToPlayer(new PacketLetterInfo(type, tradeInfo, recipient), player);
	}

	private static IMailAddress getRecipient(String recipientName, EnumAddressee type) {
		switch (type) {
			case PLAYER: {
				GameProfile gameProfile = MinecraftServer.getServer().getPlayerProfileCache().getGameProfileForUsername(recipientName);
				if (gameProfile == null) {
					gameProfile = new GameProfile(new UUID(0, 0), recipientName);
				}
				return PostManager.postRegistry.getMailAddress(gameProfile);
			}
			case TRADER: {
				return PostManager.postRegistry.getMailAddress(recipientName);
			}
			default:
				return null;
		}
	}

	public IMailAddress getRecipient() {
		if (getLetter().getRecipients().length > 0) {
			return getLetter().getRecipients()[0];
		} else {
			return null;
		}
	}

	public String getText() {
		return getLetter().getText();
	}

	public void setText(String text) {
		getLetter().setText(text);

		PacketString packet = new PacketString(PacketId.LETTER_TEXT, text);
		Proxies.net.sendToServer(packet);

	}

	public void handleSetText(PacketString packet) {
		String text = packet.getString();
		getLetter().setText(text);
	}

	/* Managing Trade info */
	private void updateTradeInfo(World world, IMailAddress address) {
		// Updating is done by the server.
		if (!Proxies.common.isSimulating(world)) {
			return;
		}

		if (address == null) {
			setTradeInfo(null);
			return;
		}

		ITradeStation station = PostManager.postRegistry.getTradeStation(world, address);
		if (station == null) {
			setTradeInfo(null);
			return;
		}

		setTradeInfo(station.getTradeInfo());
	}

	public void handleLetterInfoUpdate(PacketLetterInfo packet) {
		carrierType = packet.type;
		if (packet.type == EnumAddressee.PLAYER) {
			getLetter().setRecipient(packet.address);
		} else if (packet.type == EnumAddressee.TRADER) {
			this.setTradeInfo(packet.tradeInfo);
		}
	}

	public TradeStationInfo getTradeInfo() {
		return this.tradeInfo;
	}

	private void setTradeInfo(TradeStationInfo info) {
		this.tradeInfo = info;
		if (tradeInfo == null) {
			getLetter().setRecipient(null);
		} else {
			getLetter().setRecipient(tradeInfo.address);
		}
	}
}
