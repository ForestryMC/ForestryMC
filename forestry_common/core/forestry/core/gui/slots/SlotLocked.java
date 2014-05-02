/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotLocked extends SlotForestry {

	public SlotLocked(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
		setCanAdjustPhantom(false);
		setCanShift(false);
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack itemStack) {
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return false;
	}

	@Override
	public boolean getHasStack() {
		return false;
	}

	@Override
	public ItemStack decrStackSize(int i) {
		return null;
	}
}
