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
	public final ItemLiquidContainer canEmpty;
	public final ItemLiquidContainer waxCapsuleEmpty;
	public final ItemLiquidContainer refractoryEmpty;

	private final Table<EnumContainerType, String, ItemLiquidContainer> containers = HashBasedTable.create();

	public ItemLiquidContainer getContainer(EnumContainerType type, Fluids fluid) {
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
				ItemLiquidContainer liquidContainer;
				if (drinkProperties == null) {
					liquidContainer = new ItemLiquidContainer(type, color);
				} else {
					liquidContainer = new ItemLiquidContainerDrinkable(type, color, drinkProperties);
				}

				String name = type.getName() + '.' + fluidType.getTag();
				registerItem(liquidContainer, name);

				containers.put(type, fluidType.getFluid().getName(), liquidContainer);
			}
		}
	}

	private static ItemLiquidContainer registerEmptyContainer(EnumContainerType type, String name) {
		return registerItem(new ItemLiquidContainer(type, 0), name);
	}
}
