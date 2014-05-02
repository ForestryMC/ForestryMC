/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import forestry.core.gui.slots.SlotItemInventory;
import forestry.core.gui.slots.SlotLocked;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemInventory;

public abstract class ContainerItemInventory extends ContainerForestry {

	protected final EntityPlayer player;
	protected final ItemInventory inventory;

	public ContainerItemInventory(ItemInventory inventory, EntityPlayer player) {
		super(inventory);
		this.inventory = inventory;
		this.player = player;
	}

	public ItemInventory getItemInventory() {
		return inventory;
	}

	protected void addSecuredSlot(IInventory other, int slot, int x, int y) {
		if (other.getStackInSlot(slot) != null && inventory.itemClass.isAssignableFrom(other.getStackInSlot(slot).getItem().getClass()))
			addSlot(new SlotLocked(other, slot, x, y));
		else
			addSlot(new SlotItemInventory(this, other, player, slot, x, y));
	}

	protected abstract boolean isAcceptedItem(EntityPlayer player, ItemStack stack);

	public void purgeBag(EntityPlayer player) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null)
				continue;

			if (isAcceptedItem(player, stack))
				continue;

			Proxies.common.dropItemPlayer(player, stack);
			inventory.setInventorySlotContents(i, null);
		}
	}

	public void saveInventory(EntityPlayer entityplayer) {
		if (inventory.isItemInventory)
			inventory.onGuiSaved(entityplayer);
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		if (!Proxies.common.isSimulating(player.worldObj))
			return;

		purgeBag(player);
		saveInventory(player);
	}
}
