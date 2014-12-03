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

import org.apache.commons.lang3.StringUtils;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import forestry.api.mail.ILetter;
import forestry.api.mail.IPostalCarrier;
import forestry.api.mail.ITradeStation;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.PostManager;
import forestry.api.mail.TradeStationInfo;
import forestry.api.mail.EnumAddressee;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.slots.SlotClosed;
import forestry.core.gui.slots.SlotCustom;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.mail.Letter;
import forestry.mail.items.ItemLetter;
import forestry.mail.items.ItemLetter.LetterInventory;
import forestry.mail.items.ItemStamps;
import forestry.mail.network.PacketLetterInfo;

public class ContainerLetter extends ContainerItemInventory {

	private final LetterInventory letterInventory;
	private EnumAddressee carrierType = EnumAddressee.PLAYER;
	private TradeStationInfo tradeInfo = null;

	public ContainerLetter(EntityPlayer player, LetterInventory inventory) {
		super(inventory, player);

		letterInventory = inventory;

		// Rip open delivered mails
		if (Proxies.common.isSimulating(player.worldObj) && letterInventory.getLetter().isProcessed() && inventory.parent != null
				&& ItemLetter.getState(inventory.parent.getItemDamage()) < 2)
			inventory.parent.setItemDamage(ItemLetter.encodeMeta(2, ItemLetter.getSize(inventory.parent.getItemDamage())));

		// Init slots
		Object[] validStamps = new Object[] { ItemStamps.class };
		if (letterInventory.getLetter().isProcessed())
			validStamps = new Object[] {};

		// Stamps
		for (int i = 0; i < 4; i++)
			addSlot(new SlotCustom(inventory, Letter.SLOT_POSTAGE_1 + i, 150, 14 + i * 19, validStamps).setStackLimit(1));

		// Attachments
		if (!letterInventory.getLetter().isProcessed())
			for (int i = 0; i < 2; i++)
				for (int j = 0; j < 9; j++)
					addSlot(new SlotCustom(inventory, Letter.SLOT_ATTACHMENT_1 + j + i * 9, 17 + j * 18, 98 + i * 18, ItemLetter.class).setExclusion(true));
		else
			for (int i = 0; i < 2; i++)
				for (int j = 0; j < 9; j++)
					addSlot(new SlotClosed(inventory, Letter.SLOT_ATTACHMENT_1 + j + i * 9, 17 + j * 18, 98 + i * 18));

		// Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSecuredSlot(player.inventory, j + i * 9 + 9, 17 + j * 18, 145 + i * 18);
		// Player hotbar
		for (int i = 0; i < 9; i++)
			addSecuredSlot(player.inventory, i, 17 + i * 18, 203);

		// Set recipient type
		if (letterInventory.getLetter() != null)
			if (letterInventory.getLetter().getRecipients() != null)
				if (letterInventory.getLetter().getRecipients().length > 0)
					this.carrierType = letterInventory.getLetter().getRecipients()[0].getType();
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {

		if (Proxies.common.isSimulating(entityplayer.worldObj)) {
			ILetter letter = letterInventory.getLetter();
			if (!letter.isProcessed()) {
				IMailAddress sender = PostManager.postRegistry.getMailAddress(entityplayer.getGameProfile());
				letter.setSender(sender);
			}
		}

		super.onContainerClosed(entityplayer);
	}

	@Override
	protected boolean isAcceptedItem(EntityPlayer player, ItemStack stack) {
		return true;
	}

	public ILetter getLetter() {
		return letterInventory.getLetter();
	}

	public void setCarrierType(EnumAddressee type) {
		this.carrierType = type;
	}

	public EnumAddressee getCarrierType() {
		return this.carrierType;
	}

	public void advanceCarrierType() {
		Iterator<IPostalCarrier> it = PostManager.postRegistry.getRegisteredCarriers().values().iterator();
		while(it.hasNext()) {
			if(it.next().getType().equals(carrierType))
				break;
		}

		IPostalCarrier postal = null;
		if(it.hasNext())
			postal = it.next();
		else
			postal = PostManager.postRegistry.getRegisteredCarriers().values().iterator().next();

		setCarrierType(postal.getType());
	}

	public void setRecipient(String recipientName, EnumAddressee type) {
		if (StringUtils.isBlank(recipientName) || type == null)
			return;

		// / Send to server
		PacketPayload payload = new PacketPayload(0,0,2);
		payload.stringPayload[0] = recipientName;
		payload.stringPayload[1] = type.toString();

		PacketUpdate packet = new PacketUpdate(PacketIds.LETTER_RECIPIENT, payload);
		Proxies.net.sendToServer(packet);
	}

	public void handleSetRecipient(EntityPlayer player, PacketUpdate packet) {
		String recipientName = packet.payload.stringPayload[0];
		String typeName = packet.payload.stringPayload[1];

		EnumAddressee type = EnumAddressee.fromString(typeName);
		IMailAddress recipient;
		if (type == EnumAddressee.PLAYER) {
			GameProfile gameProfile = MinecraftServer.getServer().func_152358_ax().func_152655_a(recipientName);
			if (gameProfile == null)
				gameProfile = new GameProfile(new UUID(0, 0), recipientName);
			recipient = PostManager.postRegistry.getMailAddress(gameProfile);
		} else if (type == EnumAddressee.TRADER) {
			recipient = PostManager.postRegistry.getMailAddress(recipientName);
		} else {
			return;
		}

		getLetter().setRecipient(recipient);
		
		// Update the trading info
		updateTradeInfo(player.worldObj, recipient);
		
		// Update info on client
		Proxies.net.sendToPlayer(new PacketLetterInfo(PacketIds.LETTER_INFO, type, tradeInfo, recipient), player);
	}

	public IMailAddress getRecipient() {
		if (getLetter().getRecipients().length > 0)
			return getLetter().getRecipients()[0];
		else
			return null;
	}

	public String getText() {
		return getLetter().getText();
	}

	public void setText(String text) {
		getLetter().setText(text);

		// / Send to server
		PacketPayload payload = new PacketPayload(0, 0, 1);
		payload.stringPayload[0] = text;

		PacketUpdate packet = new PacketUpdate(PacketIds.LETTER_TEXT, payload);
		Proxies.net.sendToServer(packet);

	}

	public void handleSetText(PacketUpdate packet) {
		getLetter().setText(packet.payload.stringPayload[0]);
	}

	/* Managing Trade info */
	public void updateTradeInfo(World world, IMailAddress address) {
		// Updating is done by the server.
		if (!Proxies.common.isSimulating(world))
			return;

		if (address.isPlayer())
			return;

		ITradeStation station = PostManager.postRegistry.getTradeStation(world, address);
		if (station == null)
			return;

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
	}
}
