/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraftforge.fluids.FluidStack;

/**
 * Provides an interface to the recipe manager of the still.
 * 
 * The manager is initialized at the beginning of Forestry's BaseMod.load() cycle. Begin adding recipes in BaseMod.ModsLoaded() and this shouldn't be null even
 * if your mod loads before Forestry.
 * 
 * Accessible via {@link RecipeManagers}
 * 
 * Note that this is untested with anything other than biomass->biofuel conversion.
 * 
 * @author SirSengir
 */
public interface IStillManager extends ICraftingProvider {
	/**
	 * Add a recipe to the still
	 * 
	 * @param cyclesPerUnit
	 *            Amount of work cycles required to run through the conversion once.
	 * @param input
	 *            ItemStack representing the input liquid.
	 * @param output
	 *            ItemStack representing the output liquid
	 */
	public void addRecipe(int cyclesPerUnit, FluidStack input, FluidStack output);
}
