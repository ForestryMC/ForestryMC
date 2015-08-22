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
package forestry.core.inventory.wrappers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;

/**
 * Wrapper class used to bake the side variable into the object itself instead
 * of passing it around to all the inventory tools.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class SidedInventoryMapper implements ISidedInventory {

	private final ISidedInventory inv;
	private final int side;
	private boolean checkItems = true;

	public SidedInventoryMapper(ISidedInventory inv, EnumFacing side) {
		this(inv, side, true);
	}

	public SidedInventoryMapper(ISidedInventory inv, EnumFacing side, boolean checkItems) {
		this.inv = inv;
		this.side = side.ordinal();
		this.checkItems = checkItems;
	}

	public IInventory getBaseInventory() {
		return inv;
	}

	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return inv.decrStackSize(slot, amount);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		inv.setInventorySlotContents(slot, itemstack);
	}

	@Override
	public int getInventoryStackLimit() {
		return inv.getInventoryStackLimit();
	}

	@Override
	public void markDirty() {
		inv.markDirty();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inv.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openInventory(EntityPlayer player) {
		inv.openInventory(player);
	}
	
	

	@Override
	public void closeInventory(EntityPlayer player) {
		inv.closeInventory(player);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inv.getStackInSlotOnClosing(slot);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return !checkItems || inv.isItemValidForSlot(slot, stack);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return inv.getSlotsForFace(side);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing direction) {
		return !checkItems || inv.canInsertItem(slot, stack, direction);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing direction) {
		return !checkItems || inv.canExtractItem(slot, stack, direction);
	}

	@Override
	public int getField(int id) {
		return inv.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		inv.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return inv.getFieldCount();
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public String getCommandSenderName() {
		return inv.getCommandSenderName();
	}

	@Override
	public boolean hasCustomName() {
		return inv.hasCustomName();
	}

	@Override
	public IChatComponent getDisplayName() {
		return inv.getDisplayName();
	}

}
