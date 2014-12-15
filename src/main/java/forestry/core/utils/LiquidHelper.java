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
package forestry.core.utils;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.RecipeManagers;
import forestry.core.config.ForestryItem;
import forestry.core.fluids.Fluids;

public class LiquidHelper {

	public static boolean isEmptyLiquidData() {
		return FluidContainerRegistry.getRegisteredFluidContainerData().length <= 0;
	}

	public static void injectLiquidContainer(Fluids fluid, int volume, ItemStack filled, ItemStack empty) {
		injectLiquidContainer(fluid, volume, filled, empty, null, 0);
	}

	public static void injectWaxContainer(Fluids fluid, int volume, ItemStack filled, ItemStack empty) {
		injectLiquidContainer(fluid, volume, filled, empty, ForestryItem.beeswax.getItemStack(), 10);
	}

	public static void injectRefractoryContainer(Fluids fluid, int volume, ItemStack filled, ItemStack empty) {
		injectLiquidContainer(fluid, volume, filled, empty, ForestryItem.refractoryWax.getItemStack(), 10);
	}

	public static void injectTinContainer(Fluids fluid, int volume, ItemStack filled, ItemStack empty) {
		injectLiquidContainer(fluid, volume, filled, empty, ForestryItem.ingotTin.getItemStack(), 5);
	}

	public static void injectLiquidContainer(Fluids fluid, int volume, ItemStack filled, ItemStack empty, ItemStack remnant, int chance) {
		FluidStack contained = fluid.get(volume);
		if (contained == null)
			throw new IllegalArgumentException(String.format("Attempted to inject a liquid container for the non-existent liquid '%s'.", fluid));

		FluidContainerData container = new FluidContainerData(contained, filled, empty);
		FluidContainerRegistry.registerFluidContainer(container);

		if (RecipeManagers.squeezerManager != null)
			if (!container.filledContainer.getItem().hasContainerItem(container.filledContainer))
				if (remnant != null)
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { container.filledContainer }, container.fluid, remnant, chance);
				else
					RecipeManagers.squeezerManager.addRecipe(10, new ItemStack[] { container.filledContainer }, container.fluid);
	}

}
