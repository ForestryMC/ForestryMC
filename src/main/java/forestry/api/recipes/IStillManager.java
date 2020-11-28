/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Set;

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
    @Nullable
    IStillRecipe findMatchingRecipe(RecipeManager manager, @Nullable FluidStack item);

    boolean matches(@Nullable IStillRecipe recipe, @Nullable FluidStack item);

    Set<Fluid> getRecipeFluidInputs(RecipeManager manager);

    Set<Fluid> getRecipeFluidOutputs(RecipeManager manager);
}
