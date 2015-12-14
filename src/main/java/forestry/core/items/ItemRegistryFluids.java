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

import net.minecraft.init.Blocks;

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
		canEmpty = registerEmptyContainer(EnumContainerType.CAN, "canEmpty");
		waxCapsuleEmpty = registerEmptyContainer(EnumContainerType.CAPSULE, "waxCapsule");
		refractoryEmpty = registerEmptyContainer(EnumContainerType.REFRACTORY, "refractoryEmpty");


		for (Fluids fluidType : Fluids.values()) {
			if (fluidType.getFluid() == null) {
				continue;
			}
			for (EnumContainerType type : fluidType.getContainerTypes()) {
				ItemLiquidContainer liquidContainer = new ItemLiquidContainer(type, fluidType.getBlock(), fluidType.getColor());
				fluidType.setProperties(liquidContainer);

				String name = type.getContainerNameKey() + fluidType.getContainerNameKey();
				registerItem(liquidContainer, name);

				containers.put(type, fluidType.getFluid().getName(), liquidContainer);
			}
		}
	}

	private static ItemLiquidContainer registerEmptyContainer(EnumContainerType type, String name) {
		return registerItem(new ItemLiquidContainer(type, Blocks.air, null), name);
	}
}
