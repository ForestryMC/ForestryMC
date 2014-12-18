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
package forestry.mail.gadgets;

import forestry.api.core.ForestryAPI;
import forestry.api.mail.IStamps;
import forestry.api.mail.PostManager;
import forestry.core.gadgets.TileBase;
import forestry.core.inventory.TileInventoryAdapter;
import forestry.core.network.GuiId;
import forestry.core.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class MachinePhilatelist extends TileBase implements IInventory {

	// / CONSTANTS
	public static final short SLOT_FILTER = 0;
	public static final short SLOT_BUFFER_1 = 1;
	public static final short SLOT_BUFFER_COUNT = 27;

	public MachinePhilatelist() {
		setInternalInventory(new TileInventoryAdapter(this, 28, "INV"));
	}

	@Override
	public String getInventoryName() {
		return getUnlocalizedName();
	}

	@Override
	public void openGui(EntityPlayer player, TileBase tile) {
		player.openGui(ForestryAPI.instance, GuiId.PhilatelistGUI.ordinal(), worldObj, xCoord, yCoord, zCoord);
	}

	// / UPDATING
	@Override
	public void updateServerSide() {
		if (worldObj.getTotalWorldTime() % 20 * 10 != 0)
			return;

		ItemStack stamp = null;

		TileInventoryAdapter inventory = getInternalInventory();
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
		return getInternalInventory().getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return getInternalInventory().getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return getInternalInventory().decrStackSize(i, j);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		getInternalInventory().setInventorySlotContents(i, itemstack);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getInternalInventory().getStackInSlotOnClosing(slot);
	}

	@Override
	public int getInventoryStackLimit() {
		return getInternalInventory().getInventoryStackLimit();
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return getInternalInventory().isUseableByPlayer(player);
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		return getInternalInventory().isItemValidForSlot(slotIndex, itemstack);
	}

}
