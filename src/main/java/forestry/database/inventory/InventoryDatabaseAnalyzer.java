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
package forestry.database.inventory;

import net.minecraft.world.item.ItemStack;

import forestry.apiculture.features.ApicultureItems;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.database.tiles.TileDatabase;

public class InventoryDatabaseAnalyzer extends InventoryAdapterTile<TileDatabase> {
	public static final int SLOT_ENERGY = 0;

	public InventoryDatabaseAnalyzer(TileDatabase database) {
		super(database, 1, "AnalyzerItems");
	}

	public static boolean isAlyzingFuel(ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return false;
		}

		return ApicultureItems.HONEY_DROPS.itemEqual(itemstack) || ApicultureItems.HONEYDEW.itemEqual(itemstack);
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return isAlyzingFuel(itemStack);
	}
}
