/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraftforge.fluids.FluidStack;

/**
 * Provides an interface to the recipe manager of the hygroregulator and habitatformer.
 * <p>
 * The manager is initialized at the beginning of Forestry's BaseMod.load()
 * cycle. Begin adding recipes in BaseMod.ModsLoaded() and this shouldn't be
 * null even if your mod loads before Forestry.
 * <p>
 * Accessible via {@link RecipeManagers}
 *
 * @author Nedelosk
 */
public interface IHygroregulatorManager extends ICraftingProvider<IHygroregulatorRecipe> {

	/**
	 * Add a recipe to the alveary hygroregulator and the habitatformer.
	 *
	 * @param resource     FluidStack containing information on fluid and amount.
	 * @param transferTime The time between the removal of the fluid from the tank and the actual addition to the alveary climate.
	 * @param tempChange   The temperature change that this recipe causes in one work cycle.
	 * @param humidChange  The humidity change that this recipe causes in one work cycle.
	 */
	void addRecipe(FluidStack resource, int transferTime, float tempChange, float humidChange);
}
