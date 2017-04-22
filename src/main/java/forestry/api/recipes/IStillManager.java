/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraftforge.fluids.FluidStack;

/**
 * Provides an interface to the recipe manager of the still.
 * <p>
 * The manager is initialized at the beginning of Forestry's BaseMod.load() cycle. Begin adding recipes in BaseMod.ModsLoaded() and this shouldn't be null even
 * if your mod loads before Forestry.
 * <p>
 * Accessible via {@link RecipeManagers}
 * <p>
 * Note that this is untested with anything other than biomass->biofuel conversion.
 *
 * @author SirSengir
 */
public interface IStillManager extends ICraftingProvider<IStillRecipe> {
	/**
	 * Add a recipe to the still
	 *
	 * @param cyclesPerUnit Amount of work cycles required to run through the conversion once.
	 * @param input         FluidStack representing the input liquid.
	 * @param output        FluidStack representing the output liquid
	 */
	void addRecipe(int cyclesPerUnit, FluidStack input, FluidStack output);
}
