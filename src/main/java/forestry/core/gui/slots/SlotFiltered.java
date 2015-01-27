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

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import forestry.core.interfaces.IFilterSlotDelegate;

/**
 * Slot which only takes specific items, specified by the ICustomSlotInventory.
 */
public class SlotFiltered extends SlotWatched {

	protected final IFilterSlotDelegate filterSlotDelegate;

	public <T extends IInventory & IFilterSlotDelegate> SlotFiltered(T filterSlotDelegateInventory, int slotIndex, int xPos, int yPos) {
		this(filterSlotDelegateInventory, filterSlotDelegateInventory, slotIndex, xPos, yPos);
	}

	public SlotFiltered(IFilterSlotDelegate filterSlotDelegate, IInventory inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
		this.filterSlotDelegate = filterSlotDelegate;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		int slotIndex = getSlotIndex();
		if (filterSlotDelegate.isLocked(slotIndex)) {
			return false;
		}
		if (itemstack != null) {
			return filterSlotDelegate.canSlotAccept(slotIndex, itemstack);
		}
		return true;
	}
}
