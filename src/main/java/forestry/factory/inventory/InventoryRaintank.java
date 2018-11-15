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
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

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
			IFluidHandler fluidHandler = FluidUtil.getFluidHandler(itemStack);
			if (fluidHandler != null) {
				return fluidHandler.fill(new FluidStack(FluidRegistry.WATER, Integer.MAX_VALUE), false) > 0;
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemstack, EnumFacing side) {
		return slotIndex == SLOT_PRODUCT;
	}
}
