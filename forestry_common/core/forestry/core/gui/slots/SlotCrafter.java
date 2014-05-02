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
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.core.interfaces.ICrafter;

public class SlotCrafter extends Slot {

	ICrafter crafter;

	public SlotCrafter(IInventory inventory, ICrafter crafter, int slot, int xPos, int yPos) {
		super(inventory, slot, xPos, yPos);
		this.crafter = crafter;
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return false;
	}

	@Override
	public ItemStack decrStackSize(int amount) {
		if (!this.getHasStack())
			return null;

		return this.getStack();
	}

	@Override
    public boolean canTakeStack(EntityPlayer player) {
        return crafter.canTakeStack(getSlotIndex());
    }

	@Override
	public ItemStack getStack() {
		return this.crafter.getResult();
	}

	@Override
	public boolean getHasStack() {
		return this.getStack() != null && crafter.canTakeStack(getSlotIndex());
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack itemStack) {
		crafter.takenFromSlot(getSlotIndex(), true, player);
	}
}
