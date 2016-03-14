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
package forestry.greenhouse.multiblock;

import forestry.core.fluids.FluidHelper;
import forestry.core.fluids.TankManager;
import forestry.core.inventory.InventoryAdapterRestricted;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class InventoryGreenhouse extends InventoryAdapterRestricted {

	private final GreenhouseController greenhouseController;
	
	public InventoryGreenhouse(GreenhouseController greenhouseController) {
		super(1, "Items", greenhouseController.getAccessHandler());
		this.greenhouseController = greenhouseController;
	}
	
	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack itemStack) {
		Fluid fluid = FluidHelper.getFluidInContainer(itemStack);
		return greenhouseController.getTankManager().accepts(fluid);
	}

	public void drainCan(TankManager tankManager) {
		FluidHelper.drainContainers(tankManager, this, 0);
	}
	
}
