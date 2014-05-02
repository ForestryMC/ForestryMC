/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.inventory.wrappers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Creates a deep copy of an existing IInventory.
 *
 * Useful for performing inventory manipulations and then examining the results
 * without affecting the original inventory.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryCopy implements IInventory {

	private final IInventory orignal;
	private final ItemStack contents[];

	public InventoryCopy(IInventory orignal) {
		this.orignal = orignal;
		contents = new ItemStack[orignal.getSizeInventory()];
		for (int i = 0; i < contents.length; i++) {
			ItemStack stack = orignal.getStackInSlot(i);
			if (stack != null) {
				contents[i] = stack.copy();
			}
		}
	}

	@Override
	public int getSizeInventory() {
		return contents.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return contents[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (contents[i] != null) {
			if (contents[i].stackSize <= j) {
				ItemStack itemstack = contents[i];
				contents[i] = null;
				markDirty();
				return itemstack;
			}
			ItemStack itemstack1 = contents[i].splitStack(j);
			if (contents[i].stackSize <= 0) {
				contents[i] = null;
			}
			markDirty();
			return itemstack1;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		contents[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		markDirty();
	}

	@Override
	public String getInventoryName() {
		return orignal.getInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return orignal.getInventoryStackLimit();
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
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
		return orignal.getStackInSlotOnClosing(slot);
	}

	@Override
	public boolean hasCustomInventoryName() {
		return orignal.hasCustomInventoryName();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return orignal.isItemValidForSlot(slot, stack);
	}
}
