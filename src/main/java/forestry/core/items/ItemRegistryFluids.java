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
package forestry.core.items;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import forestry.core.fluids.Fluids;

public class ItemRegistryFluids extends ItemRegistry {
	public final ItemFluidContainerForestry canEmpty;
	public final ItemFluidContainerForestry waxCapsuleEmpty;
	public final ItemFluidContainerForestry refractoryEmpty;

	private final Table<EnumContainerType, String, ItemFluidContainerForestry> containers = HashBasedTable.create();

	public ItemFluidContainerForestry getContainer(EnumContainerType type, Fluids fluid) {
		return containers.get(type, fluid.getFluid().getName());
	}

	public ItemRegistryFluids() {
		canEmpty = registerEmptyContainer(EnumContainerType.CAN, "can.empty");
		waxCapsuleEmpty = registerEmptyContainer(EnumContainerType.CAPSULE, "capsule.empty");
		refractoryEmpty = registerEmptyContainer(EnumContainerType.REFRACTORY, "refractory.empty");

		for (Fluids fluidType : Fluids.values()) {
			if (fluidType.getFluid() == null) {
				continue;
			}
			for (EnumContainerType type : fluidType.getContainerTypes()) {
				int color = fluidType.getColor().getRGB();

				DrinkProperties drinkProperties = fluidType.getDrinkProperties();
				ItemFluidContainerForestry liquidContainer;
				if (drinkProperties == null) {
					liquidContainer = new ItemFluidContainerForestry(type, color);
				} else {
					liquidContainer = new ItemFluidContainerForestryDrinkable(type, color, drinkProperties);
				}

				String name = type.getName() + '.' + fluidType.getTag();
				registerItem(liquidContainer, name);

				containers.put(type, fluidType.getFluid().getName(), liquidContainer);
			}
		}
	}

	private static ItemFluidContainerForestry registerEmptyContainer(EnumContainerType type, String name) {
		return registerItem(new ItemFluidContainerForestry(type, 0), name);
	}
}
