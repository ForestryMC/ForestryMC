/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IFabricatorSmeltingRecipe extends IForestryRecipe {
	/**
	 * @return item to be melted down
	 */
	ItemStack getResource();

	/**
	 * @return temperature at which the item melts. Glass is 1000, Sand is 3000.
	 */
	int getMeltingPoint();

	/**
	 * @return resulting fluid
	 */
	FluidStack getProduct();
}
