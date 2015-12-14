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
package forestry.core.tiles;

import net.minecraft.item.ItemStack;

public interface IFilterSlotDelegate {

	/**
	 * Non-automation version of IInventory's isItemValidForSlot.
	 * Used to determine if a player can place a stack in a slot.
	 *
	 * Combine this with Forestry's access permissions to implement isItemValidForSlot.
	 */
	boolean canSlotAccept(int slotIndex, ItemStack itemStack);

	/**
	 * Used to lock slots under special conditions.
	 * Locked slots will have an X over them.
	 */
	boolean isLocked(int slotIndex);

}
