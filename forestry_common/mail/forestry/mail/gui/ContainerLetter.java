/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail.gui;

import java.util.Iterator;
import java.util.Locale;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.mail.ILetter;
import forestry.api.mail.IPostalCarrier;
import forestry.api.mail.ITradeStation;
import forestry.api.mail.MailAddress;
import forestry.api.mail.PostManager;
import forestry.api.mail.TradeStationInfo;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.slots.SlotClosed;
import forestry.core.gui.slots.SlotCustom;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketPayload;
import forestry.core.network.PacketUpdate;
import forestry.core.proxy.Proxies;
import forestry.mail.EnumAddressee;
import forestry.mail.Letter;
import forestry.mail.items.ItemLetter;
import forestry.mail.items.ItemLetter.LetterInventory;
import forestry.mail.items.ItemStamps;
import forestry.mail.network.PacketTradeInfo;

public class ContainerLetter extends ContainerItemInventory {

	private final LetterInventory letterInventory;
	private String carrier = EnumAddressee.PLAYER.toString().toLowerCase(Locale.ENGLISH);
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
					addSlot(new SlotCustom(inventory, Letter.SLOT_ATTACHMENT_1 + j + i * 9, 17 + j * 18, 98 + i * 18, true, new Object[] { ItemLetter.class }));
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
					this.carrier = letterInventory.getLetter().getRecipients()[0].getType();
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {

		if (Proxies.common.isSimulating(entityplayer.worldObj)) {
			ILetter letter = letterInventory.getLetter();
			if (!letter.isProcessed())
				letter.setSender(new MailAddress(entityplayer.getGameProfile().getId()));
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

	public void setCarrierType(String type) {
		this.carrier = type;
	}

	public String getCarrierType() {
		return this.carrier;
	}

	public void advanceCarrierType() {
		Iterator<IPostalCarrier> it = PostManager.postRegistry.getRegisteredCarriers().values().iterator();
		while(it.hasNext()) {
			if(it.next().getUID().equals(carrier))
				break;
		}

		IPostalCarrier postal = null;
		if(it.hasNext())
			postal = it.next();
		else
			postal = PostManager.postRegistry.getRegisteredCarriers().values().iterator().next();

		setCarrierType(postal.getUID());
	}

	public void setRecipient(MailAddress address) {
		getLetter().setRecipient(address);
		carrier = address.getType();

		// / Send to server
		PacketPayload payload = new PacketPayload(0, 0, 2);
		payload.stringPayload[0] = this.getRecipient().getIdentifier();
		payload.stringPayload[1] = this.carrier;

		PacketUpdate packet = new PacketUpdate(PacketIds.LETTER_RECIPIENT, payload);
		Proxies.net.sendToServer(packet);
	}

	public void handleSetRecipient(EntityPlayer player, PacketUpdate packet) {
		MailAddress recipient = new MailAddress(packet.payload.stringPayload[0], packet.payload.stringPayload[1]);
		getLetter().setRecipient(recipient);
		// Update the trading info
		updateTradeInfo(player.worldObj, recipient);
		// Update trade info on client
		Proxies.net.sendToPlayer(new PacketTradeInfo(PacketIds.TRADING_INFO, tradeInfo), player);
	}

	public MailAddress getRecipient() {
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
	public void updateTradeInfo(World world, MailAddress address) {
		// Updating is done by the server.
		if (!Proxies.common.isSimulating(world))
			return;

		if (!address.getType().equals(EnumAddressee.TRADER.toString().toLowerCase(Locale.ENGLISH)))
			return;

		ITradeStation station = PostManager.postRegistry.getTradeStation(world, address.getIdentifier());
		if (station == null)
			return;

		setTradeInfo(station.getTradeInfo());
	}

	public void handleTradeInfoUpdate(PacketTradeInfo packet) {
		this.setTradeInfo(packet.tradeInfo);
	}

	public TradeStationInfo getTradeInfo() {
		return this.tradeInfo;
	}

	private void setTradeInfo(TradeStationInfo info) {
		this.tradeInfo = info;
	}
}
