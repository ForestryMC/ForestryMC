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

import forestry.core.tiles.IFilterSlotDelegate;

/**
 * Useful for InventoryTweaks. Works like SlotFiltered but allows InventoryTweaks to sort it.
 */
public class SlotFilteredInventory extends SlotFiltered {
	public <T extends IInventory & IFilterSlotDelegate> SlotFilteredInventory(T inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
	}
}
