/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.WorldSavedData;

import com.mojang.authlib.GameProfile;

import forestry.api.mail.ILetter;
import forestry.api.mail.PostManager;
import forestry.core.utils.InventoryAdapter;

public class POBox extends WorldSavedData implements IInventory {

	public static final String SAVE_NAME = "POBox_";
	public static final short SLOT_SIZE = 84;

	private GameProfile owner;
	private final InventoryAdapter letters = new InventoryAdapter(SLOT_SIZE, "Letters");

	public POBox(GameProfile owner, boolean isUser) {
		super(SAVE_NAME + owner);
		this.owner = owner;
	}

	public POBox(String savename) {
		super(savename);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (nbttagcompound.hasKey("owner")) {
			owner = NBTUtil.func_152459_a(nbttagcompound.getCompoundTag("owner"));
		}
		letters.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		if (this.owner != null) {
			NBTTagCompound nbt = new NBTTagCompound();
			NBTUtil.func_152460_a(nbt, owner);
			nbttagcompound.setTag("owner", nbt);
		}
		letters.writeToNBT(nbttagcompound);
	}

	public GameProfile getOwnerProfile() {
		return this.owner;
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
		return this.letters.tryAddStack(letterstack, true);
	}

	public POBoxInfo getPOBoxInfo() {
		int playerLetters = 0;
		int tradeLetters = 0;
		for (int i = 0; i < letters.getSizeInventory(); i++) {
			if (letters.getStackInSlot(i) == null)
				continue;
			ILetter letter = new Letter(letters.getStackInSlot(i).getTagCompound());
			if (letter.getSender().isPlayer())
				playerLetters++;
			else
				tradeLetters++;
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
		return true;
	}

}
