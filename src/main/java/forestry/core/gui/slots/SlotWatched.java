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
package forestry.core.gui.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import forestry.core.inventory.watchers.FakeSlotChangeWatcher;
import forestry.core.inventory.watchers.FakeSlotPickupWatcher;
import forestry.core.inventory.watchers.ISlotChangeWatcher;
import forestry.core.inventory.watchers.ISlotPickupWatcher;

/**
 * Slot with a watcher callbacks.
 */
public class SlotWatched extends SlotForestry {
	private ISlotPickupWatcher pickupWatcher = FakeSlotPickupWatcher.instance;
	private ISlotChangeWatcher changeWatcher = FakeSlotChangeWatcher.instance;

	public SlotWatched(IInventory inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
	}

	public SlotWatched setPickupWatcher(ISlotPickupWatcher pickupWatcher) {
		this.pickupWatcher = pickupWatcher;
		return this;
	}

	public SlotWatched setChangeWatcher(ISlotChangeWatcher changeWatcher) {
		this.changeWatcher = changeWatcher;
		return this;
	}

	@Override
	public ItemStack onTake(EntityPlayer player, ItemStack itemStack) {
		itemStack = super.onTake(player, itemStack);
		pickupWatcher.onTake(getSlotIndex(), player);
		return itemStack;
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();
		changeWatcher.onSlotChanged(inventory, getSlotIndex());
	}
}
