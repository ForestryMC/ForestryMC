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
package forestry.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.api.core.INBTTagable;
import forestry.core.utils.Utils;

public class ItemInventory implements IInventory, INBTTagable {

	public final Class<? extends Item> itemClass;
	public boolean isItemInventory = false;
	public ItemStack parent;
	protected ItemStack[] inventoryStacks;

	protected ItemInventory(Class<? extends Item> itemClass) {
		this.itemClass = itemClass;
	}

	public ItemInventory(Class<? extends Item> itemClass, int slots) {
		this(itemClass);
		inventoryStacks = new ItemStack[slots];
	}

	public ItemInventory(Class<? extends Item> itemClass, int size, ItemStack itemstack) {
		this(itemClass, size);

		parent = itemstack;
		isItemInventory = true;

		// Set an uid to identify the itemstack on SMP
		setUID(false);

		readFromNBT(itemstack.getTagCompound());
	}

	public static int getOccupiedSlotCount(ItemStack itemStack) {
		NBTTagCompound nbt = itemStack.getTagCompound();
		if (nbt == null)
			return 0;

		int count = 0;
		if (nbt.hasKey("Items")) {
			NBTTagList nbttaglist = nbt.getTagList("Items", 10);
			for (int i = 0; i < nbttaglist.tagCount(); i++) {
				NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				ItemStack itemStack1 = ItemStack.loadItemStackFromNBT(nbttagcompound1);
				if (itemStack1 != null && itemStack1.stackSize > 0) {
					count++;
				}
			}
		}
		return count;
	}

	protected void setUID(boolean override) {
		if (parent.getTagCompound() == null)
			parent.setTagCompound(new NBTTagCompound());

		NBTTagCompound nbt = parent.getTagCompound();
		if (override || !nbt.hasKey("UID")) {
			nbt.setInteger("UID", Utils.getUID());
		}
	}

	public void onGuiSaved(EntityPlayer player) {
		parent = findParent(player);
		if (parent != null)
			save();
	}

	public ItemStack findParent(EntityPlayer player) {
		if (parent == null)
			return null;

		NBTTagCompound parentNBT = parent.getTagCompound();
		if (parentNBT == null)
			return null;

		int uid = parentNBT.getInteger("UID");

		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (stack == null)
				continue;
			if(stack.getItem() != parent.getItem())
				continue;
			NBTTagCompound slotNBT = stack.getTagCompound();
			if (slotNBT == null)
				continue;
			if (uid == slotNBT.getInteger("UID"))
				return player.inventory.getStackInSlot(i);
		}
		return null;
	}

	public boolean matchesUID(int otherId) {
		if (parent == null)
			return false;

		NBTTagCompound nbt = parent.getTagCompound();
		if (nbt == null)
			return false;

		int uid = nbt.getInteger("UID");
		return uid == otherId;
	}

	public void save() {
		NBTTagCompound nbt = parent.getTagCompound();
		if (nbt == null)
			nbt = new NBTTagCompound();
		writeToNBT(nbt);
		parent.setTagCompound(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		if (nbt == null)
			return;

		if (nbt.hasKey("Items")) {
			NBTTagList nbttaglist = nbt.getTagList("Items", 10);
			inventoryStacks = new ItemStack[getSizeInventory()];
			for (int i = 0; i < nbttaglist.tagCount(); i++) {
				NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				byte byte0 = nbttagcompound1.getByte("Slot");
				if (byte0 >= 0 && byte0 < inventoryStacks.length)
					inventoryStacks[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < inventoryStacks.length; i++)
			if (inventoryStacks[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				inventoryStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		nbt.setTag("Items", nbttaglist);

	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (inventoryStacks[i] == null)
			return null;

		ItemStack product;
		if (inventoryStacks[i].stackSize <= j) {
			product = inventoryStacks[i];
			inventoryStacks[i] = null;
			return product;
		} else {
			product = inventoryStacks[i].splitStack(j);
			if (inventoryStacks[i].stackSize == 0)
				inventoryStacks[i] = null;

			return product;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventoryStacks[i] = itemstack;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventoryStacks[i];
	}

	@Override
	public int getSizeInventory() {
		return inventoryStacks.length;
	}

	@Override
	public String getInventoryName() {
		return "BeeBag";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (inventoryStacks[slot] == null)
			return null;
		ItemStack toReturn = inventoryStacks[slot];
		inventoryStacks[slot] = null;
		return toReturn;
	}
}
