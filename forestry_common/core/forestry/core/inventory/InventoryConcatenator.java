/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Allows you to deal with multiple inventories through a single interface.
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class InventoryConcatenator implements IInventory {

	private final List<Integer> slotMap = new ArrayList<Integer>();
	private final List<IInventory> invMap = new ArrayList<IInventory>();

	private InventoryConcatenator() {
	}

	public static InventoryConcatenator make() {
		return new InventoryConcatenator();
	}

	public InventoryConcatenator add(IInventory inv) {
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			slotMap.add(slot);
			invMap.add(inv);
		}
		return this;
	}

	@Override
	public int getSizeInventory() {
		return slotMap.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return invMap.get(slot).getStackInSlot(slotMap.get(slot));
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return invMap.get(slot).decrStackSize(slotMap.get(slot), amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return invMap.get(slot).getStackInSlotOnClosing(slotMap.get(slot));
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		invMap.get(slot).setInventorySlotContents(slotMap.get(slot), stack);
	}

	@Override
	public String getInventoryName() {
		return "";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
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
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return invMap.get(slot).isItemValidForSlot(slotMap.get(slot), stack);
	}
}
