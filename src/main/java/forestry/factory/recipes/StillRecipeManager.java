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
package forestry.factory.recipes;

import forestry.api.recipes.IStillManager;
import forestry.api.recipes.IStillRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class StillRecipeManager extends AbstractCraftingProvider<IStillRecipe> implements IStillManager {
    private final Set<Fluid> recipeFluidInputs = new HashSet<>();
    private final Set<Fluid> recipeFluidOutputs = new HashSet<>();

    public StillRecipeManager() {
        super(IStillRecipe.TYPE);
    }

    @Override
    @Nullable
    public IStillRecipe findMatchingRecipe(RecipeManager manager, @Nullable FluidStack item) {
        if (item == null) {
            return null;
        }

        for (IStillRecipe recipe : getRecipes(manager)) {
            if (matches(recipe, item)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public boolean matches(@Nullable IStillRecipe recipe, @Nullable FluidStack item) {
        if (recipe == null || item == null) {
            return false;
        }

        return item.containsFluid(recipe.getInput());
    }

    @Override
    public Set<Fluid> getRecipeFluidInputs(RecipeManager manager) {
        if (recipeFluidInputs.isEmpty()) {
            for (IStillRecipe recipe : getRecipes(manager)) {
                FluidStack fluidStack = recipe.getInput();
                if (!fluidStack.isEmpty()) {
                    recipeFluidInputs.add(fluidStack.getFluid());
                }
            }
        }

        return Collections.unmodifiableSet(recipeFluidInputs);
    }

    @Override
    public Set<Fluid> getRecipeFluidOutputs(RecipeManager manager) {
        if (recipeFluidOutputs.isEmpty()) {
            for (IStillRecipe recipe : getRecipes(manager)) {
                FluidStack fluidStack = recipe.getOutput();
                if (!fluidStack.isEmpty()) {
                    recipeFluidOutputs.add(fluidStack.getFluid());
                }
            }
        }

        return Collections.unmodifiableSet(recipeFluidOutputs);
    }
}
