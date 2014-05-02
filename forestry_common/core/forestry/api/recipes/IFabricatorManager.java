/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

public interface IFabricatorManager extends ICraftingProvider {

	void addRecipe(ItemStack plan, FluidStack molten, ItemStack result, Object[] pattern);

	void addSmelting(ItemStack resource, FluidStack molten, int meltingPoint);

}
