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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import forestry.core.gui.slots.SlotLocked;
import forestry.core.inventory.ItemInventory;

public abstract class ContainerItemInventory<I extends ItemInventory> extends ContainerForestry {

	protected final I inventory;

	protected ContainerItemInventory(I inventory, InventoryPlayer playerInventory, int xInv, int yInv) {
		this.inventory = inventory;

		addPlayerInventory(playerInventory, xInv, yInv);
	}

	@Override
	protected void addHotbarSlot(InventoryPlayer playerInventory, int slot, int x, int y) {
		ItemStack stackInSlot = playerInventory.getStackInSlot(slot);

		if (inventory.isParentItemInventory(stackInSlot)) {
			addSlotToContainer(new SlotLocked(playerInventory, slot, x, y));
		} else {
			addSlotToContainer(new Slot(playerInventory, slot, x, y));
		}
	}

	@Override
	protected final boolean canAccess(EntityPlayer player) {
		return canInteractWith(player);
	}

	@Override
	public final boolean canInteractWith(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public final ItemStack slotClick(int slotIndex, int button, int modifier, EntityPlayer player) {
		ItemStack result = super.slotClick(slotIndex, button, modifier, player);
		inventory.onSlotClick(player);
		return result;
	}

}
