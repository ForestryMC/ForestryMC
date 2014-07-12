/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.core.INBTTagable;
import forestry.api.mail.ILetter;
import forestry.api.mail.IStamps;
import forestry.api.mail.MailAddress;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.StringUtil;

public class Letter implements ILetter, INBTTagable {

	// CONSTANTS
	public static short SLOT_ATTACHMENT_1 = 0;
	public static short SLOT_POSTAGE_1 = 18;

	private boolean isProcessed = false;

	private MailAddress sender;
	private MailAddress[] recipient;

	private String text;
	private InventoryAdapter inventory = new InventoryAdapter(22, "INV");

	public Letter(MailAddress sender, MailAddress recipient) {
		this.sender = sender;
		this.recipient = new MailAddress[] { recipient };
	}

	public Letter(NBTTagCompound nbttagcompound) {
		if (nbttagcompound != null)
			readFromNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		this.isProcessed = nbttagcompound.getBoolean("PRC");
		this.sender = MailAddress.loadFromNBT(nbttagcompound.getCompoundTag("SDR"));

		int recipientCount = nbttagcompound.getShort("CRC");
		this.recipient = new MailAddress[recipientCount];
		for (int i = 0; i < recipientCount; i++)
			this.recipient[i] = MailAddress.loadFromNBT(nbttagcompound.getCompoundTag("RC" + i));

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
		return inventory.getStacks(SLOT_POSTAGE_1, 4);
	}

	@Override
	public ItemStack[] getAttachments() {
		return inventory.getStacks(SLOT_ATTACHMENT_1, 18);
	}

	@Override
	public int countAttachments() {

		int count = 0;
		for (ItemStack stack : getAttachments())
			if (stack != null)
				count++;

		return count;

	}

	@Override
	public void addAttachment(ItemStack itemstack) {
		inventory.tryAddStack(itemstack, false);
	}

	@Override
	public void addAttachments(ItemStack[] itemstacks) {
		for (ItemStack stack : itemstacks)
			addAttachment(stack);
	}

	@Override
	public void invalidatePostage() {
		for (int i = SLOT_POSTAGE_1; i < SLOT_POSTAGE_1 + 4; i++)
			inventory.setInventorySlotContents(i, null);
	}

	public void setInventory(InventoryAdapter inventory) {
		this.inventory = inventory;
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
		if (this.isProcessed)
			return false;

		// Requires at least one recipient
		if (this.recipient != null && this.recipient.length > 0)
			return true;

		return false;
	}

	@Override
	public boolean isPostPaid() {

		int posted = 0;

		for (ItemStack stamp : getPostage()) {
			if (stamp == null)
				continue;
			if (!(stamp.getItem() instanceof IStamps))
				continue;

			posted += ((IStamps) stamp.getItem()).getPostage(stamp).getValue() * stamp.stackSize;
		}

		return posted >= requiredPostage();
	}

	@Override
	public int requiredPostage() {

		int required = 1;
		for (ItemStack attach : getAttachments())
			if (attach != null)
				required++;

		return required;
	}

	@Override
	public void addStamps(ItemStack stamps) {
		this.inventory.tryAddStack(stamps, SLOT_POSTAGE_1, 4, false);
	}

	@Override
	public boolean hasRecipient() {
		if (getRecipients().length <= 0)
			return false;

		MailAddress recipient = getRecipients()[0];
		if (recipient == null)
			return false;

		if (recipient.getProfile() == null)
			return false;

		return true;
	}

	@Override
	public void setSender(MailAddress address) {
		this.sender = address;
	}

	@Override
	public MailAddress getSender() {
		return sender;
	}

	public void setRecipients(MailAddress[] recipients) {
		this.recipient = recipients;
	}

	@Override
	public void setRecipient(MailAddress address) {
		if (address == null)
			this.recipient = new MailAddress[] {};
		else
			this.recipient = new MailAddress[] { address };
	}

	@Override
	public MailAddress[] getRecipients() {
		return recipient;
	}

	@Override
	public String getRecipientString() {
		String recipientString = "";
		for (MailAddress address : recipient) {
			if (recipientString.length() > 0)
				recipientString += ", ";
			recipientString += address.getProfile().getName();
		}

		return recipientString;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addTooltip(List list) {
		if (this.sender != null)
			list.add(StringUtil.localize("gui.mail.from") + ": " + this.sender.getProfile().getName());
		if (this.recipient != null && this.recipient.length > 0)
			list.add(StringUtil.localize("gui.mail.to") + ": " + this.getRecipientString());
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
	public String getInventoryName() {
		return inventory.getInventoryName();
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
	public void openInventory() {
		inventory.openInventory();
	}

	@Override
	public void closeInventory() {
		inventory.closeInventory();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return inventory.hasCustomInventoryName();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return inventory.isItemValidForSlot(i, itemstack);
	}
}
