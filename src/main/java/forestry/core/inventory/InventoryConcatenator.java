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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

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
	public String getCommandSenderName() {
		return "";
	}

	@Override
	public boolean hasCustomName() {
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
	public void openInventory(EntityPlayer entityplayer) {
	}

	@Override
	public void closeInventory(EntityPlayer entityplayer) {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return invMap.get(slot).isItemValidForSlot(slotMap.get(slot), stack);
	}

	@Override
	public IChatComponent getDisplayName() {
		return null;
	}

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
