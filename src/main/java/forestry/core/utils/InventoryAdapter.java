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
package forestry.core.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.api.core.INBTTagable;
import forestry.core.config.Defaults;

/**
 * With permission from Krapht.
 */
public class InventoryAdapter implements IInventory, INBTTagable {

	private IInventory inventory = null;
	//private boolean debug = false;

	public InventoryAdapter(int size, String name) {
		this(size, name, 64);
	}

	public InventoryAdapter(int size, String name, int stackLimit) {
		this(new PlainInventory(size, name, stackLimit));
	}

	public InventoryAdapter(IInventory inventory) {
		this.inventory = inventory;
		configureSided();
	}

	/*
	public InventoryAdapter enableDebug() {
		this.debug = true;
		return this;
	}
	 */

	/**
	 * @return Copy of this inventory. Stacks are copies.
	 */
	public InventoryAdapter copy() {
		InventoryAdapter copy = new InventoryAdapter(inventory.getSizeInventory(), inventory.getInventoryName(), inventory.getInventoryStackLimit());

		for (int i = 0; i < inventory.getSizeInventory(); i++)
			if (inventory.getStackInSlot(i) != null)
				copy.setInventorySlotContents(i, inventory.getStackInSlot(i).copy());

		return copy;
	}

	public ItemStack[] getStacks() {
		ItemStack[] stacks = new ItemStack[inventory.getSizeInventory()];
		for(int i = 0; i < inventory.getSizeInventory(); i++)
			stacks[i] = inventory.getStackInSlot(i);
		return stacks;
	}

	public ItemStack[] getStacks(int slot1, int length) {
		ItemStack[] result = new ItemStack[length];
		for(int i = slot1; i < slot1 + length; i++)
			result[i - slot1] = inventory.getStackInSlot(i);
		return result;
	}

	public boolean tryAddStacksCopy(ItemStack[] stacks, boolean all) {

		boolean addedAll = true;
		for (ItemStack stack : stacks) {
			if (stack == null)
				continue;

			if (!tryAddStack(stack.copy(), all))
				addedAll = false;
		}

		return addedAll;
	}

	public boolean tryAddStacks(ItemStack[] stacks, boolean all) {

		boolean addedAll = true;
		for (ItemStack stack : stacks) {
			if (stack == null)
				continue;

			if (!tryAddStack(stack, all))
				addedAll = false;
		}

		return addedAll;
	}

	public boolean tryAddStack(ItemStack stack, boolean all) {
		return tryAddStack(stack, 0, this.getSizeInventory(), all);
	}

	/**
	 * Tries to add a stack to the specified slot range.
	 * 
	 * @param stack
	 * @param startSlot
	 * @param slots
	 * @param all
	 * @return
	 */
	public boolean tryAddStack(ItemStack stack, int startSlot, int slots, boolean all) {
		return tryAddStack(stack, startSlot, slots, all, true);
	}

	public boolean tryAddStack(ItemStack stack, int startSlot, int slots, boolean all, boolean doAdd) {
		return addStack(stack, startSlot, slots, all, doAdd) > 0;
	}

	public int addStack(ItemStack stack, boolean all, boolean doAdd) {
		return addStack(stack, 0, this.getSizeInventory(), all, doAdd);
	}

	public int addStack(ItemStack stack, int startSlot, int slots, boolean all, boolean doAdd) {

		int added = 0;
		// Add to existing stacks first
		for (int i = startSlot; i < startSlot + slots; i++) {

			// Empty slot. Add
			if (inventory.getStackInSlot(i) == null)
				continue;

			// Already occupied by different item, skip this slot.
			if (!inventory.getStackInSlot(i).isItemEqual(stack))
				continue;
			if (!ItemStack.areItemStackTagsEqual(inventory.getStackInSlot(i), stack))
				continue;

			int remain = stack.stackSize - added;
			int space = inventory.getStackInSlot(i).getMaxStackSize() - inventory.getStackInSlot(i).stackSize;
			// No space left, skip this slot.
			if (space <= 0)
				continue;
			// Enough space
			if (space >= remain) {
				if (doAdd)
					inventory.getStackInSlot(i).stackSize += remain;
				return stack.stackSize;
			}

			// Not enough space
			if (doAdd)
				inventory.getStackInSlot(i).stackSize = inventory.getStackInSlot(i).getMaxStackSize();

			added += space;
		}

		if (added >= stack.stackSize)
			return added;

		for (int i = startSlot; i < startSlot + slots; i++) {
			if (inventory.getStackInSlot(i) != null)
				continue;

			if (doAdd) {
				setInventorySlotContents(i, stack.copy());
				inventory.getStackInSlot(i).stackSize = stack.stackSize - added;
			}
			return stack.stackSize;

		}

		return added;

	}

	/* CONTAINS */
	public boolean contains(ItemStack[] query, int startSlot, int slots) {
		for (ItemStack queried : query) {

			int itemCount = 0;
			for (int i = startSlot; i < startSlot + slots; i++) {
				ItemStack stack = inventory.getStackInSlot(i);

				if (stack == null)
					continue;

				if (StackUtils.equals(Blocks.bedrock, queried)) {
					itemCount += stack.stackSize;
					continue;
				}
				if (queried.getItemDamage() < 0) {

					if (stack.getItem() == queried.getItem())
						itemCount += stack.stackSize;
					continue;
				}

				if (stack.isItemEqual(queried) && ItemStack.areItemStackTagsEqual(stack, queried))
					itemCount += stack.stackSize;
			}

			if (itemCount < queried.stackSize)
				return false;

		}

		return true;
	}

	/* REMOVAL */
	public void removeResources(ItemStack[] query, int startSlot, int slots) {
		for (ItemStack queried : query) {

			ItemStack remain = queried.copy();

			for (int i = startSlot; i < startSlot + slots; i++) {
				ItemStack stack = inventory.getStackInSlot(i);

				if (stack == null)
					continue;

				if (queried.getItemDamage() < 0) {

					if (stack.getItem() == queried.getItem()) {
						ItemStack removed = decrStackSize(i, remain.stackSize);
						remain.stackSize -= removed.stackSize;
					}

				} else if (stack.isItemEqual(remain) && ItemStack.areItemStackTagsEqual(stack, remain)) {
					ItemStack removed = decrStackSize(i, remain.stackSize);
					remain.stackSize -= removed.stackSize;
				}

				if (remain.stackSize <= 0)
					break;
			}

		}

	}

	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slotId) {
		return inventory.getStackInSlot(slotId);
	}

	@Override
	public ItemStack decrStackSize(int slotId, int count) {
		return inventory.decrStackSize(slotId, count);
	}

	@Override
	public void setInventorySlotContents(int slotId, ItemStack itemstack) {
		inventory.setInventorySlotContents(slotId, itemstack);
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
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		return inventory.getStackInSlotOnClosing(slotIndex);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return false;
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

	/* ISIDEDINVENTORY */
	private int[][] slotMap;

	public int[] getSizeInventorySide(int side) {
		return slotMap[side];
	}

	private void configureSided() {
		slotMap = new int[6][0];
		configureSided(Defaults.FACINGS, 0, getSizeInventory());
	}

	public InventoryAdapter configureSidedUp(int startSlot, int count) {
		return configureSided(Defaults.FACING_UP, startSlot, count);
	}

	public InventoryAdapter configureSidedDown(int startSlot, int count) {
		return configureSided(Defaults.FACING_DOWN, startSlot, count);
	}

	public InventoryAdapter configureSidedNorthSouth(int startSlot, int count) {
		return configureSided(Defaults.FACING_NORTHSOUTH, startSlot, count);
	}

	public InventoryAdapter configureSidedWestEast(int startSlot, int count) {
		return configureSided(Defaults.FACING_WESTEAST, startSlot, count);
	}

	public InventoryAdapter configureSidedSides(int startSlot, int count) {
		return configureSided(Defaults.FACING_SIDES, startSlot, count);
	}

	public InventoryAdapter configureSided(int side, int startSlot, int count) {
		return configureSided(new int[] { side }, startSlot, count);
	}

	public InventoryAdapter configureSided(int[] sides, int startSlot, int count) {
		int[] slots = new int[count];
		for(int i = 0; i < count; i++)
			slots[i] = startSlot + i;

		return configureSided(sides, slots);
	}

	public InventoryAdapter configureSided(int[] sides, int[] slots) {
		for(int side : sides)
			slotMap[side] = slots;

		return this;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (!nbttagcompound.hasKey(inventory.getInventoryName()))
			return;

		NBTTagList nbttaglist = nbttagcompound.getTagList(inventory.getInventoryName(), 10);

		for (int j = 0; j < nbttaglist.tagCount(); ++j) {
			NBTTagCompound nbttagcompound2 = nbttaglist.getCompoundTagAt(j);
			int index = nbttagcompound2.getByte("Slot");
			inventory.setInventorySlotContents(index, ItemStack.loadItemStackFromNBT(nbttagcompound2));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < inventory.getSizeInventory(); i++)
			if (inventory.getStackInSlot(i) != null) {
				NBTTagCompound nbttagcompound2 = new NBTTagCompound();
				nbttagcompound2.setByte("Slot", (byte) i);
				inventory.getStackInSlot(i).writeToNBT(nbttagcompound2);
				nbttaglist.appendTag(nbttagcompound2);
			}
		nbttagcompound.setTag(inventory.getInventoryName(), nbttaglist);
	}

}
