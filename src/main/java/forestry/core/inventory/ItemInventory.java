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

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.core.tiles.IFilterSlotDelegate;

public abstract class ItemInventory implements IInventory, IFilterSlotDelegate {
	private static final String KEY_ITEMS = "Items"; // legacy
	private static final String KEY_SLOTS = "Slots";
	private static final String KEY_UID = "UID";
	private static final Random rand = new Random();

	private final EntityPlayer player;
	private final ItemStack parent;
	private final ItemStack[] inventoryStacks;

	public ItemInventory(EntityPlayer player, int size, ItemStack parent) {
		this.player = player;
		this.parent = parent;
		this.inventoryStacks = new ItemStack[size];

		setUID(); // Set a uid to identify the itemstack on SMP

		readFromNBT(parent.getTagCompound());
	}

	public static int getOccupiedSlotCount(ItemStack itemStack) {
		NBTTagCompound nbt = itemStack.getTagCompound();
		if (nbt == null) {
			return 0;
		}

		if (nbt.hasKey(KEY_SLOTS)) {
			NBTTagCompound slotNbt = nbt.getCompoundTag(KEY_SLOTS);
			return slotNbt.func_150296_c().size();
		}

		int count = 0;
		if (nbt.hasKey(KEY_ITEMS)) { // legacy since Forestry 3.6
			NBTTagList nbttaglist = nbt.getTagList(KEY_ITEMS, 10);
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

	private void setUID() {
		ItemStack parent = getParent();

		if (parent.getTagCompound() == null) {
			parent.setTagCompound(new NBTTagCompound());
		}

		NBTTagCompound nbt = parent.getTagCompound();
		if (!nbt.hasKey(KEY_UID)) {
			nbt.setInteger(KEY_UID, rand.nextInt());
		}
	}

	public boolean isParentItemInventory(ItemStack itemStack) {
		ItemStack parent = getParent();
		return isSameItemInventory(parent, itemStack);
	}

	protected ItemStack getParent() {
		ItemStack equipped = player.getCurrentEquippedItem();
		if (isSameItemInventory(equipped, parent)) {
			return equipped;
		}
		return parent;
	}

	private static boolean isSameItemInventory(ItemStack base, ItemStack comparison) {
		if (base == null || comparison == null) {
			return false;
		}

		if (base.getItem() != comparison.getItem()) {
			return false;
		}

		if (!base.hasTagCompound() || !comparison.hasTagCompound()) {
			return false;
		}

		String baseUID = base.getTagCompound().getString(KEY_UID);
		String comparisonUID = comparison.getTagCompound().getString(KEY_UID);
		return baseUID != null && comparisonUID != null && baseUID.equals(comparisonUID);
	}

	public void readFromNBT(NBTTagCompound nbt) {

		if (nbt == null) {
			return;
		}

		if (nbt.hasKey(KEY_SLOTS)) {
			NBTTagCompound nbtSlots = nbt.getCompoundTag(KEY_SLOTS);
			for (int i = 0; i < inventoryStacks.length; i++) {
				String slotKey = getSlotNBTKey(i);
				if (nbtSlots.hasKey(slotKey)) {
					NBTTagCompound itemNbt = nbtSlots.getCompoundTag(slotKey);
					ItemStack itemStack = ItemStack.loadItemStackFromNBT(itemNbt);
					inventoryStacks[i] = itemStack;
				} else {
					inventoryStacks[i] = null;
				}
			}
		} else {

			// legacy since Forestry 3.6
			if (nbt.hasKey(KEY_ITEMS)) {
				for (int i = 0; i < inventoryStacks.length; i++) {
					inventoryStacks[i] = null;
				}

				NBTTagList nbttaglist = nbt.getTagList(KEY_ITEMS, 10);
				for (int i = 0; i < nbttaglist.tagCount(); i++) {
					NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
					byte byte0 = nbttagcompound1.getByte("Slot");
					if (byte0 >= 0 && byte0 < inventoryStacks.length) {
						ItemStack itemStack = ItemStack.loadItemStackFromNBT(nbttagcompound1);
						inventoryStacks[byte0] = itemStack;
					}
				}

				writeToParentNBT();
			}
		}
	}

	private void writeToParentNBT() {
		ItemStack parent = getParent();
		if (parent == null) {
			return;
		}

		NBTTagCompound nbt = parent.getTagCompound();
		NBTTagCompound slotsNbt = new NBTTagCompound();
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack itemStack = getStackInSlot(i);
			if (itemStack != null) {
				String slotKey = getSlotNBTKey(i);
				NBTTagCompound itemNbt = new NBTTagCompound();
				itemStack.writeToNBT(itemNbt);
				slotsNbt.setTag(slotKey, itemNbt);
			}
		}

		nbt.setTag(KEY_SLOTS, slotsNbt);
		nbt.removeTag(KEY_ITEMS);
	}

	private static String getSlotNBTKey(int i) {
		return Integer.toString(i, Character.MAX_RADIX);
	}

	public void onSlotClick(EntityPlayer player) {
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		ItemStack stack = getStackInSlot(i);
		if (stack == null) {
			return null;
		}

		if (stack.stackSize <= j) {
			setInventorySlotContents(i, null);
			return stack;
		} else {
			ItemStack product = stack.splitStack(j);
			setInventorySlotContents(i, stack);
			return product;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (itemstack != null && itemstack.stackSize == 0) {
			itemstack = null;
		}

		inventoryStacks[i] = itemstack;

		ItemStack parent = getParent();

		NBTTagCompound nbt = parent.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			parent.setTagCompound(nbt);
		}

		NBTTagCompound slotNbt;
		if (!nbt.hasKey(KEY_SLOTS)) {
			slotNbt = new NBTTagCompound();
			nbt.setTag(KEY_SLOTS, slotNbt);
		} else {
			slotNbt = nbt.getCompoundTag(KEY_SLOTS);
		}

		String slotKey = getSlotNBTKey(i);

		if (itemstack == null) {
			slotNbt.removeTag(slotKey);
		} else {
			NBTTagCompound itemNbt = new NBTTagCompound();
			itemstack.writeToNBT(itemNbt);

			slotNbt.setTag(slotKey, itemNbt);
		}
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
	public final void markDirty() {
		writeToParentNBT();
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
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack) {
		return canSlotAccept(slotIndex, itemStack);
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack toReturn = getStackInSlot(slot);

		if (toReturn != null) {
			setInventorySlotContents(slot, null);
		}

		return toReturn;
	}

	/* IFilterSlotDelegate */
	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return true;
	}

	@Override
	public boolean isLocked(int slotIndex) {
		return false;
	}
}
