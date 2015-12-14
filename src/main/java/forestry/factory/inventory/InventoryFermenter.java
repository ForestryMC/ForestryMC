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
import forestry.factory.recipes.FermenterRecipeManager;
import forestry.factory.tiles.TileFermenter;

public class InventoryFermenter extends InventoryAdapterTile<TileFermenter> {
	public static final short SLOT_RESOURCE = 0;
	public static final short SLOT_FUEL = 1;
	public static final short SLOT_CAN_OUTPUT = 2;
	public static final short SLOT_CAN_INPUT = 3;
	public static final short SLOT_INPUT = 4;

	public InventoryFermenter(TileFermenter fermenter) {
		super(fermenter, 5, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		if (slotIndex == SLOT_RESOURCE) {
			return FermenterRecipeManager.isResource(itemStack);
		} else if (slotIndex == SLOT_INPUT) {
			Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
			return tile.getTankManager().accepts(fluid);
		} else if (slotIndex == SLOT_CAN_INPUT) {
			return FluidHelper.isFillableContainer(itemStack);
		} else if (slotIndex == SLOT_FUEL) {
			return FuelManager.fermenterFuel.containsKey(itemStack);
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemstack, int side) {
		return slotIndex == SLOT_CAN_OUTPUT;
	}
}
