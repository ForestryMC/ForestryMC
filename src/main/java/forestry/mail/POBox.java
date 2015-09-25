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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

import forestry.api.mail.ILetter;
import forestry.api.mail.IMailAddress;
import forestry.api.mail.PostManager;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.utils.InventoryUtil;

public class POBox extends WorldSavedData implements IInventory {

	public static final String SAVE_NAME = "POBox_";
	public static final short SLOT_SIZE = 84;

	private IMailAddress address;
	private final InventoryAdapter letters = new InventoryAdapter(SLOT_SIZE, "Letters").disableAutomation();

	public POBox(IMailAddress address) {
		super(SAVE_NAME + address);
		if (!address.isPlayer()) {
			throw new IllegalArgumentException("POBox address must be a player");
		}
		this.address = address;
	}

	public POBox(String savename) {
		super(savename);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (nbttagcompound.hasKey("address")) {
			this.address = MailAddress.loadFromNBT(nbttagcompound.getCompoundTag("address"));
		}
		letters.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		if (this.address != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			this.address.writeToNBT(nbt);
			nbttagcompound.setTag("address", nbt);
		}
		letters.writeToNBT(nbttagcompound);
	}

	public boolean storeLetter(ItemStack letterstack) {
		ILetter letter = PostManager.postRegistry.getLetter(letterstack);

		// Mark letter as processed
		letter.setProcessed(true);
		letter.invalidatePostage();
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		letter.writeToNBT(nbttagcompound);
		letterstack.setTagCompound(nbttagcompound);

		this.markDirty();

		return InventoryUtil.tryAddStack(letters, letterstack, true);
	}

	public POBoxInfo getPOBoxInfo() {
		int playerLetters = 0;
		int tradeLetters = 0;
		for (int i = 0; i < letters.getSizeInventory(); i++) {
			if (letters.getStackInSlot(i) == null) {
				continue;
			}
			ILetter letter = new Letter(letters.getStackInSlot(i).getTagCompound());
			if (letter.getSender().isPlayer()) {
				playerLetters++;
			} else {
				tradeLetters++;
			}
		}

		return new POBoxInfo(playerLetters, tradeLetters);
	}

	/* IINVENTORY */
	@Override
	public void markDirty() {
		super.markDirty();
		letters.markDirty();
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		this.markDirty();
		letters.setInventorySlotContents(var1, var2);
	}

	@Override
	public int getSizeInventory() {
		return letters.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return letters.getStackInSlot(var1);
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		return letters.decrStackSize(var1, var2);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return letters.getStackInSlotOnClosing(var1);
	}

	@Override
	public String getInventoryName() {
		return letters.getInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return letters.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return letters.isUseableByPlayer(var1);
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return letters.isItemValidForSlot(i, itemstack);
	}

}
