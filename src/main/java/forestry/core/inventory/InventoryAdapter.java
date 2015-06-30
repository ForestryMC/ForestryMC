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

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import forestry.core.config.Defaults;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.network.IStreamable;
import forestry.core.utils.PlainInventory;

/**
 * With permission from Krapht.
 */
public class InventoryAdapter implements IInventoryAdapter, IStreamable {

	private final IInventory inventory;
	private boolean allowAutomation = true;

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

	public InventoryAdapter disableAutomation() {
		this.allowAutomation = false;
		return this;
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
			if (inventory.getStackInSlot(i) != null) {
				copy.setInventorySlotContents(i, inventory.getStackInSlot(i).copy());
			}
		}

		return copy;
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
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return true;
	}

	@Override
	public boolean isLocked(int slotIndex) {
		return false;
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
		if (allowAutomation) {
			return slotMap[side];
		}
		return Defaults.SLOTS_NONE;
	}

	private void configureSided() {
		slotMap = new int[6][0];
		InvTools.configureSided(this, Defaults.FACINGS, 0, getSizeInventory());
	}

	@Override
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
		return false;
	}

	/* SAVING & LOADING */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		if (!nbttagcompound.hasKey(inventory.getInventoryName())) {
			return;
		}

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

	@Override
	public void writeData(DataOutputStreamForestry data) throws IOException {
		data.writeInventory(inventory);
	}

	@Override
	public void readData(DataInputStreamForestry data) throws IOException {
		data.readInventory(inventory);
	}
}
