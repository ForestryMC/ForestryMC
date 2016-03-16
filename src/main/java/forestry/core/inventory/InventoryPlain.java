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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import forestry.api.core.INbtReadable;
import forestry.api.core.INbtWritable;
import forestry.core.utils.InventoryUtil;

public class InventoryPlain implements IInventory, INbtWritable, INbtReadable {

	private final ItemStack[] contents;
	private final String name;
	private final int stackLimit;

	public InventoryPlain(int size) {
		this(size, null, 64);
	}

	
	public InventoryPlain(int size, String name) {
		this(size, name, 64);
	}

	public InventoryPlain(int size, String name, int stackLimit) {
		this.contents = new ItemStack[size];
		this.name = name;
		this.stackLimit = stackLimit;
	}

	public InventoryPlain(IInventory tocopy) {
		this(tocopy.getSizeInventory(), tocopy.getName(), tocopy.getInventoryStackLimit());
		for (int i = 0; i < tocopy.getSizeInventory(); i++) {
			if (tocopy.getStackInSlot(i) != null) {
				this.setInventorySlotContents(i, tocopy.getStackInSlot(i).copy());
			} else {
				this.setInventorySlotContents(i, null);
			}
		}
	}

	public ItemStack[] getContents() {
		return contents;
	}

	@Override
	public int getSizeInventory() {
		return contents.length;
	}

	@Override
	public ItemStack getStackInSlot(int slotId) {
		return contents[slotId];
	}

	@Override
	public ItemStack decrStackSize(int slotId, int count) {
		if (contents[slotId] == null) {
			return null;
		}
		if (contents[slotId].stackSize > count) {
			return contents[slotId].splitStack(count);
		}
		ItemStack stack = contents[slotId];
		contents[slotId] = null;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slotId, ItemStack itemstack) {
		contents[slotId] = itemstack;
	}

	@Override
	public String getName() {
		if(name == null){
			return "Plain";
		}
		return name;
	}

	@Override
	public int getInventoryStackLimit() {
		return stackLimit;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return false;
	}
	
	@Override
	public ItemStack removeStackFromSlot(int slotIndex) {
		return this.getStackInSlot(slotIndex);
	}
	
	@Override
	public boolean hasCustomName() {
		return name != null;
	}
	
	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentText(getName());
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	/* INBTagable */
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		InventoryUtil.readFromNBT(this, nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		InventoryUtil.writeToNBT(this, nbttagcompound);
	}

	/* Fields */
	
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
}
