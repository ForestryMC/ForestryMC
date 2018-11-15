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
package forestry.mail.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.core.IErrorSource;
import forestry.api.core.IErrorState;
import forestry.api.mail.ILetter;
import forestry.core.errors.EnumErrorCode;
import forestry.core.inventory.ItemInventory;
import forestry.core.items.ItemWithGui;
import forestry.core.utils.SlotUtil;
import forestry.mail.Letter;
import forestry.mail.LetterProperties;
import forestry.mail.items.ItemStamps;

public class ItemInventoryLetter extends ItemInventory implements IErrorSource {
	private final ILetter letter;

	public ItemInventoryLetter(EntityPlayer player, ItemStack itemstack) {
		super(player, 0, itemstack);
		NBTTagCompound tagCompound = itemstack.getTagCompound();
		Preconditions.checkNotNull(tagCompound);
		letter = new Letter(tagCompound);
	}

	public ILetter getLetter() {
		return letter;
	}

	public void onLetterClosed() {
		ItemStack parent = getParent();
		LetterProperties.closeLetter(parent, letter);
	}

	public void onLetterOpened() {
		ItemStack parent = getParent();
		LetterProperties.openLetter(parent);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack result = letter.decrStackSize(index, count);
		NBTTagCompound tagCompound = getParent().getTagCompound();
		Preconditions.checkNotNull(tagCompound);
		letter.writeToNBT(tagCompound);
		return result;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack itemstack) {
		letter.setInventorySlotContents(index, itemstack);
		NBTTagCompound tagCompound = getParent().getTagCompound();
		Preconditions.checkNotNull(tagCompound);
		letter.writeToNBT(tagCompound);
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return letter.getStackInSlot(i);
	}

	@Override
	public int getSizeInventory() {
		return letter.getSizeInventory();
	}

	@Override
	public String getName() {
		return letter.getName();
	}

	@Override
	public int getInventoryStackLimit() {
		return letter.getInventoryStackLimit();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		return letter.isUsableByPlayer(entityplayer);
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		return letter.removeStackFromSlot(slot);
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (letter.isProcessed()) {
			return false;
		} else if (SlotUtil.isSlotInRange(slotIndex, Letter.SLOT_POSTAGE_1, Letter.SLOT_POSTAGE_COUNT)) {
			Item item = itemStack.getItem();
			return item instanceof ItemStamps;
		} else if (SlotUtil.isSlotInRange(slotIndex, Letter.SLOT_ATTACHMENT_1, Letter.SLOT_ATTACHMENT_COUNT)) {
			return !(itemStack.getItem() instanceof ItemWithGui);
		}
		return false;
	}

	/* IErrorSource */
	@Override
	public ImmutableSet<IErrorState> getErrorStates() {

		ImmutableSet.Builder<IErrorState> errorStates = ImmutableSet.builder();

		if (!letter.hasRecipient()) {
			errorStates.add(EnumErrorCode.NO_RECIPIENT);
		}

		if (!letter.isProcessed() && !letter.isPostPaid()) {
			errorStates.add(EnumErrorCode.NOT_POST_PAID);
		}

		return errorStates.build();
	}
}
