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
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;

/**
 * Wrapper class used to specify part of an existing inventory to be treated as
 * a complete inventory. Used primarily to map a side of an ISidedInventory, but
 * it is also helpful for complex inventories such as the Tunnel Bore.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class InventoryMapper implements IInventory {

	private final IInventory inv;
	private final int start;
	private final int size;
	private int stackSizeLimit = -1;
	private boolean checkItems = true;

	public InventoryMapper(IInventory inv, EnumFacing side) {
		this(inv, getInventoryStart(inv, side), getInventorySize(inv, side));
	}

	public InventoryMapper(IInventory inv) {
		this(inv, 0, inv.getSizeInventory(), true);
	}

	public InventoryMapper(IInventory inv, boolean checkItems) {
		this(inv, 0, inv.getSizeInventory(), checkItems);
	}

	/**
	 * Creates a new InventoryMapper
	 *
	 * @param inv   The backing inventory
	 * @param start The starting index
	 * @param size  The size of the new inventory, take care not to exceed the
	 *              end of the backing inventory
	 */
	public InventoryMapper(IInventory inv, int start, int size) {
		this(inv, start, size, true);
	}

	public InventoryMapper(IInventory inv, int start, int size, boolean checkItems) {
		this.inv = inv;
		this.start = start;
		this.size = size;
		this.checkItems = checkItems;
	}

	protected static int getInventorySize(IInventory inv, EnumFacing side) {
		return inv.getSizeInventory();
	}

	protected static int getInventoryStart(IInventory inv, EnumFacing side) {
		return 0;
	}

	public IInventory getBaseInventory() {
		return inv;
	}

	@Override
	public int getSizeInventory() {
		return size;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv.getStackInSlot(start + slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return inv.decrStackSize(start + slot, amount);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		inv.setInventorySlotContents(start + slot, itemstack);
	}
	
	@Override
	public String getCommandSenderName() {
		return inv.getCommandSenderName();
	}

	@Override
	public IChatComponent getDisplayName() {
		return inv.getDisplayName();
	}

	public void setStackSizeLimit(int limit) {
		stackSizeLimit = limit;
	}

	@Override
	public int getInventoryStackLimit() {
		return stackSizeLimit > 0 ? stackSizeLimit : inv.getInventoryStackLimit();
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
	public void openInventory(EntityPlayer entityplayer) {
		inv.openInventory(entityplayer);
	}

	@Override
	public void closeInventory(EntityPlayer entityplayer) {
		inv.closeInventory(entityplayer);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inv.getStackInSlotOnClosing(start + slot);
	}
	
	@Override
	public boolean hasCustomName() {
		return inv.hasCustomName();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (checkItems) {
			return inv.isItemValidForSlot(start + slot, stack);
		}
		return true;
	}
	
	/*
	 * FIELDS
	 */
	@Override
	public int getField(int id) {return 0;}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {return 0;}

	@Override
	public void clear() {}

}
