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

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import forestry.api.core.INBTTagable;
import forestry.api.mail.ILetter;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IStamps;
import forestry.core.inventory.InvTools;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.utils.StringUtil;

public class Letter implements ILetter, INBTTagable {

	// CONSTANTS
	public static final short SLOT_ATTACHMENT_1 = 0;
	public static final short SLOT_ATTACHMENT_COUNT = 18;
	public static final short SLOT_POSTAGE_1 = 18;
	public static final short SLOT_POSTAGE_COUNT = 4;

	private boolean isProcessed = false;

	private IMailAddress sender;
	private IMailAddress[] recipient;

	private String text;
	private final InventoryAdapter inventory = new InventoryAdapter(22, "INV");

	public Letter(IMailAddress sender, IMailAddress recipient) {
		this.sender = sender;
		this.recipient = new IMailAddress[] { recipient };
	}

	public Letter(NBTTagCompound nbttagcompound) {
		if (nbttagcompound != null) {
			readFromNBT(nbttagcompound);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		this.isProcessed = nbttagcompound.getBoolean("PRC");
		this.sender = MailAddress.loadFromNBT(nbttagcompound.getCompoundTag("SDR"));

		int recipientCount = nbttagcompound.getShort("CRC");
		this.recipient = new MailAddress[recipientCount];
		for (int i = 0; i < recipientCount; i++) {
			this.recipient[i] = MailAddress.loadFromNBT(nbttagcompound.getCompoundTag("RC" + i));
		}

		this.text = nbttagcompound.getString("TXT");

		this.inventory.readFromNBT(nbttagcompound);

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		nbttagcompound.setBoolean("PRC", isProcessed);

		NBTTagCompound subcompound = new NBTTagCompound();
		this.sender.writeToNBT(subcompound);
		nbttagcompound.setTag("SDR", subcompound);

		nbttagcompound.setShort("CRC", (short) recipient.length);
		for (int i = 0; i < recipient.length; i++) {
			subcompound = new NBTTagCompound();
			this.recipient[i].writeToNBT(subcompound);
			nbttagcompound.setTag("RC" + i, subcompound);
		}

		nbttagcompound.setString("TXT", this.text);

		inventory.writeToNBT(nbttagcompound);
	}

	@Override
	public ItemStack[] getPostage() {
		return InvTools.getStacks(inventory, SLOT_POSTAGE_1, SLOT_POSTAGE_COUNT);
	}

	@Override
	public ItemStack[] getAttachments() {
		return InvTools.getStacks(inventory, SLOT_ATTACHMENT_1, SLOT_ATTACHMENT_COUNT);
	}

	@Override
	public int countAttachments() {

		int count = 0;
		for (ItemStack stack : getAttachments()) {
			if (stack != null) {
				count++;
			}
		}

		return count;

	}

	@Override
	public void addAttachment(ItemStack itemstack) {
		InvTools.tryAddStack(inventory, itemstack, false);
	}

	@Override
	public void addAttachments(ItemStack[] itemstacks) {
		for (ItemStack stack : itemstacks) {
			addAttachment(stack);
		}
	}

	@Override
	public void invalidatePostage() {
		for (int i = SLOT_POSTAGE_1; i < SLOT_POSTAGE_1 + SLOT_POSTAGE_COUNT; i++) {
			inventory.setInventorySlotContents(i, null);
		}
	}

	@Override
	public void setProcessed(boolean flag) {
		this.isProcessed = flag;
	}

	@Override
	public boolean isProcessed() {
		return this.isProcessed;
	}

	@Override
	public boolean isMailable() {
		// Can't resend an already sent letter
		// Requires at least one recipient
		return !isProcessed && recipient != null && recipient.length > 0;
	}

	@Override
	public boolean isPostPaid() {

		int posted = 0;

		for (ItemStack stamp : getPostage()) {
			if (stamp == null) {
				continue;
			}
			if (!(stamp.getItem() instanceof IStamps)) {
				continue;
			}

			posted += ((IStamps) stamp.getItem()).getPostage(stamp).getValue() * stamp.stackSize;
		}

		return posted >= requiredPostage();
	}

	@Override
	public int requiredPostage() {

		int required = 1;
		for (ItemStack attach : getAttachments()) {
			if (attach != null) {
				required++;
			}
		}

		return required;
	}

	@Override
	public void addStamps(ItemStack stamps) {
		InvTools.tryAddStack(inventory, stamps, SLOT_POSTAGE_1, 4, false);
	}

	@Override
	public boolean hasRecipient() {
		if (getRecipients().length <= 0) {
			return false;
		}

		IMailAddress recipient = getRecipients()[0];
		if (recipient == null) {
			return false;
		}

		if (StringUtils.isBlank(recipient.getName())) {
			return false;
		}

		return true;
	}

	@Override
	public void setSender(IMailAddress address) {
		this.sender = address;
	}

	@Override
	public IMailAddress getSender() {
		return sender;
	}

	public void setRecipients(IMailAddress[] recipients) {
		this.recipient = recipients;
	}

	@Override
	public void setRecipient(IMailAddress address) {
		if (address == null) {
			this.recipient = new IMailAddress[] {};
		} else {
			this.recipient = new IMailAddress[] { address };
		}
	}

	@Override
	public IMailAddress[] getRecipients() {
		return recipient;
	}

	@Override
	public String getRecipientString() {
		StringBuilder recipientString = new StringBuilder();
		for (IMailAddress address : recipient) {
			if (recipientString.length() > 0) {
				recipientString.append(", ");
			}
			recipientString.append(address.getName());
		}

		return recipientString.toString();
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void addTooltip(List<String> list) {
		if (this.sender != null && StringUtils.isNotBlank(this.sender.getName())) {
			list.add(StringUtil.localize("gui.mail.from") + ": " + this.sender.getName());
		}
		if (this.recipient != null && this.recipient.length > 0) {
			list.add(StringUtil.localize("gui.mail.to") + ": " + this.getRecipientString());
		}
	}

	// / IINVENTORY
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return inventory.getStackInSlot(var1);
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		return inventory.decrStackSize(var1, var2);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return inventory.getStackInSlotOnClosing(var1);
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		inventory.setInventorySlotContents(var1, var2);
	}

	@Override
	public String getCommandSenderName() {
		return inventory.getCommandSenderName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public void markDirty() {
		inventory.markDirty();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		inventory.openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		inventory.closeInventory(player);
	}

	@Override
	public boolean hasCustomName() {
		return inventory.hasCustomName();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return inventory.isItemValidForSlot(i, itemstack);
	}

	@Override
	public int getField(int id) {
		return inventory.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		inventory.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return inventory.getFieldCount();
	}

	@Override
	public void clear() {
		inventory.clear();
	}

	@Override
	public IChatComponent getDisplayName() {
		return inventory.getDisplayName();
	}
}
