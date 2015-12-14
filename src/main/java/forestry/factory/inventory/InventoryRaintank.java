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

import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.Fluids;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.factory.tiles.TileRaintank;

public class InventoryRaintank extends InventoryAdapterTile<TileRaintank> {
	public static final short SLOT_RESOURCE = 0;
	public static final short SLOT_PRODUCT = 1;

	public InventoryRaintank(TileRaintank raintank) {
		super(raintank, 3, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (slotIndex == SLOT_RESOURCE) {
			return FluidHelper.getFilledContainer(Fluids.WATER.getFluid(1000), itemStack) != null;
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
		return slotIndex == SLOT_PRODUCT;
	}
}
