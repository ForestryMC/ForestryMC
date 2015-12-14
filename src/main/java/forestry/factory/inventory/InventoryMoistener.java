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

import net.minecraftforge.fluids.Fluid;

import forestry.api.fuels.FuelManager;
import forestry.core.fluids.FluidHelper;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.utils.SlotUtil;
import forestry.factory.recipes.MoistenerRecipeManager;
import forestry.factory.tiles.TileMoistener;

public class InventoryMoistener extends InventoryAdapterTile<TileMoistener> {
	public static final short SLOT_STASH_1 = 0;
	public static final short SLOT_STASH_COUNT = 6;
	public static final short SLOT_RESERVOIR_1 = 6;
	public static final short SLOT_RESERVOIR_COUNT = 3;
	public static final short SLOT_WORKING = 9;
	public static final short SLOT_PRODUCT = 10;
	public static final short SLOT_RESOURCE = 11;

	public InventoryMoistener(TileMoistener moistener) {
		super(moistener, 12, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (slotIndex == SLOT_RESOURCE) {
			return MoistenerRecipeManager.isResource(itemStack);
		}

		if (SlotUtil.isSlotInRange(slotIndex, SLOT_STASH_1, SLOT_STASH_COUNT)) {
			return FuelManager.moistenerResource.containsKey(itemStack);
		}

		if (slotIndex == SLOT_PRODUCT) {
			Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
			return tile.getTankManager().accepts(fluid);
		}

		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
		if (slotIndex == SLOT_PRODUCT) {
			return true;
		}

		if (SlotUtil.isSlotInRange(slotIndex, SLOT_STASH_1, (SLOT_STASH_COUNT + SLOT_RESERVOIR_COUNT))) {
			return !FuelManager.moistenerResource.containsKey(itemstack);
		}

		return false;
	}
}
