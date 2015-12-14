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
import forestry.factory.recipes.CentrifugeRecipeManager;
import forestry.factory.tiles.TileCentrifuge;

public class InventoryCentrifuge extends InventoryAdapterTile<TileCentrifuge> {
	public static final int SLOT_RESOURCE = 0;
	public static final int SLOT_PRODUCT_1 = 1;
	public static final int SLOT_PRODUCT_COUNT = 9;

	public InventoryCentrifuge(TileCentrifuge centrifuge) {
		super(centrifuge, 10, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		return slotIndex == SLOT_RESOURCE && CentrifugeRecipeManager.findMatchingRecipe(itemStack) != null;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
		return SlotUtil.isSlotInRange(slotIndex, SLOT_PRODUCT_1, SLOT_PRODUCT_COUNT);
	}
}
