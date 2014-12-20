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
package forestry.core.gui;

import forestry.core.gui.slots.SlotItemInventory;
import forestry.core.gui.slots.SlotLocked;
import forestry.core.inventory.ItemInventory;
import forestry.core.proxy.Proxies;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

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
			addSlotToContainer(new SlotLocked(other, slot, x, y));
		else
			addSlotToContainer(new SlotItemInventory(this, other, player, slot, x, y));
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
