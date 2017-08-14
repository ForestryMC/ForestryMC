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

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

import forestry.api.mail.ILetter;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.IStamps;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.Translator;

public class Letter implements ILetter {
	private static final Random rand = new Random();
	public static final short SLOT_ATTACHMENT_1 = 0;
	public static final short SLOT_ATTACHMENT_COUNT = 18;
	public static final short SLOT_POSTAGE_1 = 18;
	public static final short SLOT_POSTAGE_COUNT = 4;

	private boolean isProcessed = false;

	private IMailAddress sender;
	@Nullable
	private IMailAddress recipient;

	private String text = "";
	private final InventoryAdapter inventory = new InventoryAdapter(22, "INV");
	private final String uid;

	public Letter(IMailAddress sender, IMailAddress recipient) {
		this.sender = sender;
		this.recipient = recipient;
		this.uid = String.valueOf(rand.nextInt());
	}

	public Letter(NBTTagCompound nbttagcompound) {
		this.isProcessed = nbttagcompound.getBoolean("PRC");
		this.sender = new MailAddress(nbttagcompound.getCompoundTag("SDR"));
		this.recipient = new MailAddress(nbttagcompound.getCompoundTag("RC"));

		this.text = nbttagcompound.getString("TXT");
		this.uid = nbttagcompound.getString("UID");
		this.inventory.readFromNBT(nbttagcompound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {

		nbttagcompound.setBoolean("PRC", isProcessed);

		NBTTagCompound subcompound = new NBTTagCompound();
		this.sender.writeToNBT(subcompound);
		nbttagcompound.setTag("SDR", subcompound);

		if (this.recipient != null) {
			subcompound = new NBTTagCompound();
			this.recipient.writeToNBT(subcompound);
			nbttagcompound.setTag("RC", subcompound);
		}

		nbttagcompound.setString("TXT", this.text);
		nbttagcompound.setString("UID", this.uid);
		inventory.writeToNBT(nbttagcompound);
		return nbttagcompound;
	}

	@Override
	public NonNullList<ItemStack> getPostage() {
		return InventoryUtil.getStacks(inventory, SLOT_POSTAGE_1, SLOT_POSTAGE_COUNT);
	}

	@Override
	public NonNullList<ItemStack> getAttachments() {
		return InventoryUtil.getStacks(inventory, SLOT_ATTACHMENT_1, SLOT_ATTACHMENT_COUNT);
	}

	@Override
	public int countAttachments() {
		int count = 0;
		for (ItemStack stack : getAttachments()) {
			if (!stack.isEmpty()) {
				count++;
			}
		}

		return count;
	}

	@Override
	public void addAttachment(ItemStack itemstack) {
		InventoryUtil.tryAddStack(inventory, itemstack, false);
	}

	@Override
	public void addAttachments(NonNullList<ItemStack> itemstacks) {
		for (ItemStack stack : itemstacks) {
			addAttachment(stack);
		}
	}

	@Override
	public void invalidatePostage() {
		for (int i = SLOT_POSTAGE_1; i < SLOT_POSTAGE_1 + SLOT_POSTAGE_COUNT; i++) {
			inventory.setInventorySlotContents(i, ItemStack.EMPTY);
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
		return !isProcessed && recipient != null;
	}

	@Override
	public boolean isPostPaid() {

		int posted = 0;

		for (ItemStack stamp : getPostage()) {
			if (stamp.isEmpty()) {
				continue;
			}
			if (!(stamp.getItem() instanceof IStamps)) {
				continue;
			}

			posted += ((IStamps) stamp.getItem()).getPostage(stamp).getValue() * stamp.getCount();
		}

		return posted >= requiredPostage();
	}

	@Override
	public int requiredPostage() {

		int required = 1;
		for (ItemStack attach : getAttachments()) {
			if (!attach.isEmpty()) {
				required++;
			}
		}

		return required;
	}

	@Override
	public void addStamps(ItemStack stamps) {
		InventoryUtil.tryAddStack(inventory, stamps, SLOT_POSTAGE_1, 4, false);
	}

	@Override
	public boolean hasRecipient() {
		return recipient != null && !StringUtils.isBlank(recipient.getName());
	}

	@Override
	public void setSender(IMailAddress address) {
		this.sender = address;
	}

	@Override
	public IMailAddress getSender() {
		return sender;
	}

	@Override
	public void setRecipient(@Nullable IMailAddress address) {
		this.recipient = address;
	}

	@Override
	@Nullable
	public IMailAddress getRecipient() {
		return recipient;
	}

	@Override
	public String getRecipientString() {
		if (recipient == null) {
			return "";
		}
		return recipient.getName();
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
		if (StringUtils.isNotBlank(this.sender.getName())) {
			list.add(Translator.translateToLocal("for.gui.mail.from") + ": " + this.sender.getName());
		}
		if (this.recipient != null) {
			list.add(Translator.translateToLocal("for.gui.mail.to") + ": " + this.getRecipientString());
		}
	}

	// / IINVENTORY
	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

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
	public ItemStack removeStackFromSlot(int index) {
		return inventory.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		inventory.setInventorySlotContents(var1, var2);
	}

	@Override
	public String getName() {
		return inventory.getName();
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
	public boolean isUsableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer var1) {
		inventory.openInventory(var1);
	}

	@Override
	public void closeInventory(EntityPlayer var1) {
		inventory.closeInventory(var1);
	}

	@Override
	public boolean hasCustomName() {
		return inventory.hasCustomName();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return inventory.isItemValidForSlot(i, itemstack);
	}

	/* FIELDS */
	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}

	@Override
	public ITextComponent getDisplayName() {
		return inventory.getDisplayName();
	}
}
