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
package forestry.factory.inventory;

import net.minecraft.item.ItemStack;

import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.utils.SlotUtil;
import forestry.factory.tiles.TileWorktable;

public class InventoryWorktable extends InventoryAdapterTile<TileWorktable> {
	public final static short SLOT_INVENTORY_1 = 0;
	public final static short SLOT_INVENTORY_COUNT = 18;

	public InventoryWorktable(TileWorktable worktable) {
		super(worktable, 18, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return SlotUtil.isSlotInRange(slotIndex, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
	}
}
