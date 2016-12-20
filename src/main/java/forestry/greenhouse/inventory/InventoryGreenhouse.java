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
package forestry.greenhouse.inventory;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.InventoryAdapterRestricted;
import forestry.greenhouse.multiblock.GreenhouseController;

public class InventoryGreenhouse extends InventoryAdapterRestricted {

	private final GreenhouseController greenhouseController;
	
	public InventoryGreenhouse(GreenhouseController greenhouseController) {
		super(1, "Items");
		this.greenhouseController = greenhouseController;
	}
	
	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		FluidStack fluid = FluidUtil.getFluidContained(itemStack);
		return fluid != null && greenhouseController.getTankManager().canFillFluidType(fluid);
	}

	public void drainCan(TankManager tankManager) {
		FluidHelper.drainContainers(tankManager, this, 0);
	}
	
}
