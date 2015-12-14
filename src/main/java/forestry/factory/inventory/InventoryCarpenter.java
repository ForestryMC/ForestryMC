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

import forestry.core.fluids.FluidHelper;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.core.utils.SlotUtil;
import forestry.factory.recipes.CarpenterRecipeManager;
import forestry.factory.tiles.TileCarpenter;

public class InventoryCarpenter extends InventoryAdapterTile<TileCarpenter> {
	public final static int SLOT_BOX = 9;
	public final static int SLOT_PRODUCT = 10;
	public final static int SLOT_PRODUCT_COUNT = 1;
	public final static int SLOT_CAN_INPUT = 11;
	public final static short SLOT_INVENTORY_1 = 12;
	public final static short SLOT_INVENTORY_COUNT = 18;

	public InventoryCarpenter(TileCarpenter carpenter) {
		super(carpenter, 30, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (slotIndex == SLOT_CAN_INPUT) {
			Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
			return tile.getTankManager().accepts(fluid);
		} else if (slotIndex == SLOT_BOX) {
			return CarpenterRecipeManager.isBox(itemStack);
		} else if (canSlotAccept(SLOT_CAN_INPUT, itemStack) || canSlotAccept(SLOT_BOX, itemStack)) {
			return false;
		}

		return SlotUtil.isSlotInRange(slotIndex, SLOT_INVENTORY_1, SLOT_INVENTORY_COUNT);
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
		return slotIndex == SLOT_PRODUCT;
	}
}
