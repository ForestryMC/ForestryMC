/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail.gadgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.core.ForestryAPI;
import forestry.api.mail.IStamps;
import forestry.api.mail.PostManager;
import forestry.core.gadgets.TileBase;
import forestry.core.network.GuiId;
import forestry.core.utils.InventoryAdapter;
import forestry.core.utils.StackUtils;

public class MachinePhilatelist extends TileBase implements IInventory {

	// / CONSTANTS
	public static final short SLOT_FILTER = 0;
	public static final short SLOT_BUFFER_1 = 1;
	public static final short SLOT_BUFFER_COUNT = 27;

	private final InventoryAdapter inventory = new InventoryAdapter(28, "INV");

	public MachinePhilatelist() {
	}

	@Override
	public String getInventoryName() {
		return "mail.2";
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.PhilatelistGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
	}

	/* SAVING & LOADING */
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		inventory.writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		inventory.readFromNBT(nbttagcompound);
	}

	// / UPDATING
	@Override
	public void updateServerSide() {
		if (worldObj.getTotalWorldTime() % 20 * 10 != 0)
			return;

		ItemStack stamp = null;

		if (inventory.getStackInSlot(SLOT_FILTER) == null)
			stamp = PostManager.postRegistry.getPostOffice(worldObj).getAnyStamp(1);
		else {
			ItemStack filter = inventory.getStackInSlot(SLOT_FILTER);
			if (filter.getItem() instanceof IStamps)
				stamp = PostManager.postRegistry.getPostOffice(worldObj).getAnyStamp(((IStamps) filter.getItem()).getPostage(filter), 1);
		}

		if (stamp == null)
			return;

		// Store it.
		StackUtils.stowInInventory(stamp, inventory, true, SLOT_BUFFER_1, SLOT_BUFFER_COUNT);
	}

	/* IINVENTORY */
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public void markDirty() {
		inventory.markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return super.isUseableByPlayer(player);
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean hasCustomInventoryName() {
		return super.hasCustomInventoryName();
	}

	/**
	 * TODO: just a specialsource workaround
	 */
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		return super.isItemValidForSlot(slotIndex, itemstack);
	}

}
