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

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import forestry.core.fluids.Fluids;

public class ItemRegistryFluids extends ItemRegistry {
	public final ItemFluidContainerForestry canEmpty;
	public final ItemFluidContainerForestry waxCapsuleEmpty;
	public final ItemFluidContainerForestry refractoryEmpty;

	private final Map<EnumContainerType, ItemFluidContainerForestry> containers = new EnumMap<>(EnumContainerType.class);

	public ItemStack getContainer(EnumContainerType type, Fluids fluid) {
		return getContainer(type, fluid.getFluid());
	}

	public ItemStack getContainer(EnumContainerType type, Fluid fluid) {
		ItemStack container = new ItemStack(containers.get(type));
		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(container);
		if (fluidHandler != null) {
			fluidHandler.fill(new FluidStack(fluid, Integer.MAX_VALUE), true);
			return container;
		} else {
			return ItemStack.EMPTY;
		}
	}

	public ItemRegistryFluids() {
		canEmpty = registerItem(new ItemFluidContainerForestry(EnumContainerType.CAN), "can");
		waxCapsuleEmpty = registerItem(new ItemFluidContainerForestry(EnumContainerType.CAPSULE), "capsule");
		refractoryEmpty = registerItem(new ItemFluidContainerForestry(EnumContainerType.REFRACTORY), "refractory");

		containers.put(EnumContainerType.CAN, canEmpty);
		containers.put(EnumContainerType.CAPSULE, waxCapsuleEmpty);
		containers.put(EnumContainerType.REFRACTORY, refractoryEmpty);
	}

}
