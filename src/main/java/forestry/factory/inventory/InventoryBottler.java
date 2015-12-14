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

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.core.fluids.FluidHelper;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.factory.tiles.TileBottler;

public class InventoryBottler extends InventoryAdapterTile<TileBottler> {
	public static final short SLOT_INPUT_EMPTY_CAN = 0;
	public static final short SLOT_OUTPUT = 1;
	public static final short SLOT_INPUT_FULL_CAN = 2;

	public InventoryBottler(TileBottler tileBottler) {
		super(tileBottler, 3, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (slotIndex == SLOT_INPUT_EMPTY_CAN) {
			return FluidHelper.isFillableContainer(itemStack);
		} else if (slotIndex == SLOT_INPUT_FULL_CAN) {
			FluidStack fluidStack = FluidHelper.getFluidStackInContainer(itemStack);
			return fluidStack != null && FluidRegistry.isFluidRegistered(fluidStack.getFluid());
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
		return slotIndex == SLOT_OUTPUT;
	}
}
