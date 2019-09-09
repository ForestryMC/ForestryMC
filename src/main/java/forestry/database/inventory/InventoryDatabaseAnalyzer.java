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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.items.EnumHoneyDrop;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.database.tiles.TileDatabase;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

public class InventoryDatabaseAnalyzer extends InventoryAdapterTile<TileDatabase> {
	public static final int SLOT_ENERGY = 0;

	public InventoryDatabaseAnalyzer(TileDatabase database) {
		super(database, 1, "AnalyzerItems");
	}

	public static boolean isAlyzingFuel(ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return false;
		}

		if (ModuleHelper.isEnabled(ForestryModuleUids.APICULTURE)) {
			ItemRegistryApiculture beeItems = ModuleApiculture.getItems();

			Item item = itemstack.getItem();
			return beeItems.getHoneyDrop(EnumHoneyDrop.HONEY, 1).getItem() == item || beeItems.honeydew == item;
		}

		return false;
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return isAlyzingFuel(itemStack);
	}
}
