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

import javax.annotation.Nullable;
import java.util.Iterator;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.mail.EnumAddressee;
import forestry.api.mail.ILetter;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IPostalCarrier;
import forestry.api.mail.ITradeStation;
import forestry.api.mail.ITradeStationInfo;
import forestry.api.mail.PostManager;
import forestry.core.gui.ContainerItemInventory;
import forestry.core.gui.slots.SlotFiltered;
import forestry.core.utils.Log;
import forestry.core.utils.NetworkUtil;
import forestry.mail.Letter;
import forestry.mail.ModuleMail;
import forestry.mail.inventory.ItemInventoryLetter;
import forestry.mail.network.packets.PacketLetterInfoResponse;
import forestry.mail.network.packets.PacketLetterTextSet;

public class ContainerLetter extends ContainerItemInventory<ItemInventoryLetter> implements ILetterInfoReceiver {

	private EnumAddressee carrierType = EnumAddressee.PLAYER;
	@Nullable
	private ITradeStationInfo tradeInfo = null;

	public static ContainerLetter fromNetwork(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
		Hand hand = extraData.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
		PlayerEntity player = playerInv.player;
		ItemInventoryLetter inv = new ItemInventoryLetter(player, player.getHeldItem(hand));
		return new ContainerLetter(windowId, player, inv);
	}

	public ContainerLetter(int windowId, PlayerEntity player, ItemInventoryLetter inventory) {
		super(windowId, inventory, player.inventory, 17, 145, ModuleMail.getContainerTypes().LETTER);

		// Init slots

		// Stamps
		for (int i = 0; i < 4; i++) {
			addSlot(new SlotFiltered(inventory, Letter.SLOT_POSTAGE_1 + i, 150, 14 + i * 19).setStackLimit(1));
		}

		// Attachments
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new SlotFiltered(inventory, Letter.SLOT_ATTACHMENT_1 + j + i * 9, 17 + j * 18, 98 + i * 18));
			}
		}

		// Rip open delivered mails
		if (!player.world.isRemote) {
			if (inventory.getLetter().isProcessed()) {
				inventory.onLetterOpened();
			}
		}

		// Set recipient type
		ILetter letter = inventory.getLetter();
		IMailAddress recipient = letter.getRecipient();
		if (recipient != null) {
			this.carrierType = recipient.getType();
		}
	}

	@Override
	public void onContainerClosed(PlayerEntity PlayerEntity) {

		if (!PlayerEntity.world.isRemote) {
			ILetter letter = inventory.getLetter();
			if (!letter.isProcessed()) {
				IMailAddress sender = PostManager.postRegistry.getMailAddress(PlayerEntity.getGameProfile());
				letter.setSender(sender);
			}
		}

		inventory.onLetterClosed();

		super.onContainerClosed(PlayerEntity);
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

	public void handleRequestLetterInfo(PlayerEntity player, String recipientName, EnumAddressee type) {
		MinecraftServer server = player.getServer();
		if (server == null) {
			Log.error("Could not get server");
			return;
		}
		IMailAddress recipient = getRecipient(server, recipientName, type);

		getLetter().setRecipient(recipient);

		// Update the trading info
		if (recipient == null || recipient.getType() == EnumAddressee.TRADER) {
			updateTradeInfo(player.world, recipient);
		}

		// Update info on client
		NetworkUtil.sendToPlayer(new PacketLetterInfoResponse(type, tradeInfo, recipient), player);
	}

	@Nullable
	private static IMailAddress getRecipient(MinecraftServer minecraftServer, String recipientName, EnumAddressee type) {
		switch (type) {
			case PLAYER: {
				GameProfile gameProfile = minecraftServer.getPlayerProfileCache().getGameProfileForUsername(recipientName);
				if (gameProfile == null) {
					return null;
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

	@Nullable
	public IMailAddress getRecipient() {
		return getLetter().getRecipient();
	}

	public String getText() {
		return getLetter().getText();
	}

	@OnlyIn(Dist.CLIENT)
	public void setText(String text) {
		getLetter().setText(text);

		NetworkUtil.sendToServer(new PacketLetterTextSet(text));
	}

	public void handleSetText(String text) {
		getLetter().setText(text);
	}

	/* Managing Trade info */
	private void updateTradeInfo(World world, @Nullable IMailAddress address) {
		// Updating is done by the server.
		if (world.isRemote) {
			return;
		}

		if (address == null) {
			setTradeInfo(null);
			return;
		}

		ITradeStation station = PostManager.postRegistry.getTradeStation((ServerWorld) world, address);
		if (station == null) {
			setTradeInfo(null);
			return;
		}

		setTradeInfo(station.getTradeInfo());
	}

	@Override
	public void handleLetterInfoUpdate(EnumAddressee type, @Nullable IMailAddress address, @Nullable ITradeStationInfo tradeInfo) {
		carrierType = type;
		if (type == EnumAddressee.PLAYER) {
			getLetter().setRecipient(address);
		} else if (type == EnumAddressee.TRADER) {
			this.setTradeInfo(tradeInfo);
		}
	}

	@Nullable
	public ITradeStationInfo getTradeInfo() {
		return this.tradeInfo;
	}

	private void setTradeInfo(@Nullable ITradeStationInfo info) {
		this.tradeInfo = info;
		if (tradeInfo == null) {
			getLetter().setRecipient(null);
		} else {
			getLetter().setRecipient(tradeInfo.getAddress());
		}
	}
}
