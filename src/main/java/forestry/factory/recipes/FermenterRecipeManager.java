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

import forestry.api.recipes.IFermenterManager;
import forestry.api.recipes.IFermenterRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FermenterRecipeManager extends AbstractCraftingProvider<IFermenterRecipe> implements IFermenterManager {
    private final Set<Fluid> recipeFluidInputs = new HashSet<>();
    private final Set<Fluid> recipeFluidOutputs = new HashSet<>();

    public FermenterRecipeManager() {
        super(IFermenterRecipe.TYPE);
    }

    @Nullable
    public IFermenterRecipe findMatchingRecipe(RecipeManager manager, ItemStack res, FluidStack fluidStack) {
        if (res.isEmpty()) {
            return null;
        }

        for (IFermenterRecipe recipe : getRecipes(manager)) {
            if (matches(recipe, res, fluidStack)) {
                return recipe;
            }
        }

        return null;
    }

    public boolean matches(IFermenterRecipe recipe, ItemStack res, FluidStack fluidStack) {
        Ingredient resource = recipe.getResource();
        if (!resource.test(res)) {
            return false;
        }

        FluidStack fluid = recipe.getFluidResource();
        return fluidStack.isFluidEqual(fluid);
    }

    public boolean isResource(RecipeManager manager, ItemStack resource) {
        if (resource.isEmpty()) {
            return false;
        }

        for (IFermenterRecipe recipe : getRecipes(manager)) {
            if (recipe.getResource().test(resource)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<Fluid> getRecipeFluidInputs(RecipeManager manager) {
        if (recipeFluidInputs.isEmpty()) {
            for (IFermenterRecipe recipe : getRecipes(manager)) {
                FluidStack fluidStack = recipe.getFluidResource();
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
            for (IFermenterRecipe recipe : getRecipes(manager)) {
                recipeFluidOutputs.add(recipe.getOutput());
            }
        }

        return Collections.unmodifiableSet(recipeFluidOutputs);
    }
}
