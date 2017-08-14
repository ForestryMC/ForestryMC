/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IFabricatorSmeltingManager extends ICraftingProvider<IFabricatorSmeltingRecipe> {
	/**
	 * Add a smelting recipe to the Fabricator
	 *
	 * @param resource     item to be melted down
	 * @param molten       resulting fluid
	 * @param meltingPoint temperature at which the item melts. Glass is 1000, Sand is 3000.
	 */
	void addSmelting(ItemStack resource, FluidStack molten, int meltingPoint);
}
