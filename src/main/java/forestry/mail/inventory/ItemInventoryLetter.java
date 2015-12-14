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

import com.google.common.collect.ImmutableSet;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.core.IErrorSource;
import forestry.api.core.IErrorState;
import forestry.api.mail.ILetter;
import forestry.core.config.Config;
import forestry.core.errors.EnumErrorCode;
import forestry.core.gui.IHintSource;
import forestry.core.inventory.ItemInventory;
import forestry.core.items.ItemWithGui;
import forestry.core.utils.SlotUtil;
import forestry.mail.Letter;
import forestry.mail.LetterProperties;
import forestry.mail.items.ItemStamps;

public class ItemInventoryLetter extends ItemInventory implements IErrorSource, IHintSource {
	private ILetter letter;

	public ItemInventoryLetter(EntityPlayer player, ItemStack itemstack) {
		super(player, 0, itemstack);
	}

	public ILetter getLetter() {
		return letter;
	}

	public void onLetterClosed() {
		ItemStack parent = getParent();
		if (parent == null) {
			return;
		}

		LetterProperties.closeLetter(parent, letter);
	}

	public void onLetterOpened() {
		ItemStack parent = getParent();
		if (parent == null) {
			return;
		}

		LetterProperties.openLetter(parent);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (nbttagcompound == null) {
			return;
		}

		letter = new Letter(nbttagcompound);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		ItemStack result = letter.decrStackSize(i, j);
		letter.writeToNBT(getParent().getTagCompound());
		return result;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		letter.setInventorySlotContents(i, itemstack);
		letter.writeToNBT(getParent().getTagCompound());
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
	public String getInventoryName() {
		return letter.getInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return letter.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return letter.isUseableByPlayer(entityplayer);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return letter.getStackInSlotOnClosing(slot);
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

	/* IHintSource */
	@Override
	public List<String> getHints() {
		return Config.hints.get("letter");
	}

}
