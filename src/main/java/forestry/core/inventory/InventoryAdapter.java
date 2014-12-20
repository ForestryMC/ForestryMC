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

import forestry.api.core.INBTTagable;
import forestry.core.config.Defaults;
import forestry.core.utils.PlainInventory;
import forestry.core.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * With permission from Krapht.
 */
public class InventoryAdapter implements IInventory, ISidedInventory, INBTTagable {

	private final IInventory inventory;
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

//	public InventoryAdapter enableDebug() {
//		this.debug = true;
//		return this;
//	}
	/**
	 * @return Copy of this inventory. Stacks are copies.
	 */
	public InventoryAdapter copy() {
		InventoryAdapter copy = new InventoryAdapter(inventory.getSizeInventory(), inventory.getInventoryName(), inventory.getInventoryStackLimit());

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) != null)
				copy.setInventorySlotContents(i, inventory.getStackInSlot(i).copy());
		}

		return copy;
	}

	public ItemStack[] getStacks() {
		ItemStack[] stacks = new ItemStack[inventory.getSizeInventory()];
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			stacks[i] = inventory.getStackInSlot(i);
		}
		return stacks;
	}

	public ItemStack[] getStacks(int slot1, int length) {
		ItemStack[] result = new ItemStack[length];
		for (int i = slot1; i < slot1 + length; i++) {
			result[i - slot1] = inventory.getStackInSlot(i);
		}
		return result;
	}

	public boolean tryAddStacksCopy(ItemStack[] stacks, boolean all) {
		return tryAddStacksCopy(stacks, 0, this.getSizeInventory(), all);
	}

	public boolean tryAddStacksCopy(ItemStack[] stacks, int startSlot, int slots, boolean all) {

		for (ItemStack stack : stacks) {
			if (stack == null)
				continue;

			if (!tryAddStack(stack.copy(), startSlot, slots, all))
				return false;
		}

		return true;
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
	 */
	public boolean tryAddStack(ItemStack stack, int startSlot, int slots, boolean all) {
		return tryAddStack(stack, startSlot, slots, all, true);
	}

	public boolean tryAddStack(ItemStack stack, int startSlot, int slots, boolean all, boolean doAdd) {
		int added = addStack(stack, startSlot, slots, all, doAdd);
		if (all)
			return added == stack.stackSize;
		else
			return added > 0;
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
	public boolean contains(ItemStack query, int startSlot, int slots) {
		ItemStack[] queryArray = new ItemStack[]{query};
		return contains(queryArray, startSlot, slots);
	}

	public boolean contains(ItemStack[] query, int startSlot, int slots) {
		ItemStack[] stock = getStacks(startSlot, slots);
		return StackUtils.containsSets(query, stock) > 0;
	}

	public boolean containsPercent(float percent, int slot1, int length) {
		int amount = 0;
		int stackMax = 0;
		for (ItemStack itemStack : getStacks(slot1, length)) {
			if (itemStack == null) {
				stackMax += 64;
				continue;
			}

			amount += itemStack.stackSize;
			stackMax += itemStack.getMaxStackSize();
		}
		if (stackMax == 0)
			return false;
		return ((float) amount / (float) stackMax) >= percent;
	}

	public boolean containsAmount(int amount, int slot1, int length) {
		int total = 0;
		for (ItemStack itemStack : getStacks(slot1, length)) {
			if (itemStack == null)
				continue;

			total += itemStack.stackSize;
			if (total >= amount)
				return true;
		}
		return false;
	}

	/* REMOVAL */
	/**
	 * Removes a set of items from an inventory.
	 * Removes the exact items first if they exist, and then removes crafting equivalents.
	 * If the inventory doesn't have all the required items, returns false without removing anything.
	 * If stowContainer is true, items with containers will have their container stowed.
	 */
	public boolean removeSets(int count, ItemStack[] set, int firstSlotIndex, int slotCount, EntityPlayer player, boolean stowContainer, boolean oreDictionary, boolean craftingTools) {

		ItemStack[] condensedSet = StackUtils.condenseStacks(set, -1, false);

		ItemStack[] stock = getStacks(firstSlotIndex, slotCount);
		if (StackUtils.containsSets(condensedSet, stock, oreDictionary, craftingTools) < count)
			return false;

		for (ItemStack stackToRemove : condensedSet) {
			stackToRemove.stackSize *= count;

			// try to remove the exact stack first
			removeStack(stackToRemove, firstSlotIndex, slotCount, player, stowContainer, false, false);

			// remove crafting equivalents next
			if (stackToRemove.stackSize > 0)
				removeStack(stackToRemove, firstSlotIndex, slotCount, player, stowContainer, oreDictionary, craftingTools);
		}
		return true;
	}

	/**
	 * Private Helper for removeSetsFromInventory. Assumes removal is possible.
	 */
	private void removeStack(ItemStack stackToRemove, int firstSlotIndex, int slotCount, EntityPlayer player, boolean stowContainer, boolean oreDictionary, boolean craftingTools) {
		for (int j = firstSlotIndex; j < firstSlotIndex + slotCount; j++) {
			ItemStack stackInSlot = getStackInSlot(j);
			if (stackInSlot == null)
				continue;

			if (!StackUtils.isCraftingEquivalent(stackToRemove, stackInSlot, oreDictionary, craftingTools))
				continue;

			ItemStack removed = decrStackSize(j, stackToRemove.stackSize);
			stackToRemove.stackSize -= removed.stackSize;

			if (stowContainer && stackToRemove.getItem().hasContainerItem(stackToRemove))
				StackUtils.stowContainerItem(removed, this, j, player);

			if (stackToRemove.stackSize == 0)
				return;
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

	/* ISIDEDINVENTORY */
	private int[][] slotMap;

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
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
		return configureSided(new int[]{side}, startSlot, count);
	}

	public InventoryAdapter configureSided(int[] sides, int startSlot, int count) {
		int[] slots = new int[count];
		for (int i = 0; i < count; i++) {
			slots[i] = startSlot + i;
		}

		return configureSided(sides, slots);
	}

	public InventoryAdapter configureSided(int[] sides, int[] slots) {
		for (int side : sides) {
			slotMap[side] = slots;
		}

		return this;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return true;
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
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) != null) {
				NBTTagCompound nbttagcompound2 = new NBTTagCompound();
				nbttagcompound2.setByte("Slot", (byte) i);
				inventory.getStackInSlot(i).writeToNBT(nbttagcompound2);
				nbttaglist.appendTag(nbttagcompound2);
			}
		}
		nbttagcompound.setTag(inventory.getInventoryName(), nbttaglist);
	}

}
