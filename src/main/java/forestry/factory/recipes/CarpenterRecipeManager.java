/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.factory.recipes;

import forestry.api.recipes.ICarpenterManager;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.ItemStackUtil;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CarpenterRecipeManager extends AbstractCraftingProvider<ICarpenterRecipe> implements ICarpenterManager {
    private final Set<Fluid> recipeFluids = new HashSet<>();

    public CarpenterRecipeManager() {
        super(ICarpenterRecipe.TYPE);
    }

    @Override
    public Optional<ICarpenterRecipe> findMatchingRecipe(
            RecipeManager recipeManager,
            FluidStack liquid,
            ItemStack item,
            IInventory inventory
    ) {
        for (ICarpenterRecipe recipe : getRecipes(recipeManager)) {
            if (matches(recipe, liquid, item, inventory)) {
                return Optional.of(recipe);
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean matches(
            @Nullable ICarpenterRecipe recipe,
            FluidStack resource,
            ItemStack item,
            IInventory craftingInventory
    ) {
        if (recipe == null) {
            return false;
        }

        FluidStack liquid = recipe.getFluidResource();
        if (!liquid.isEmpty()) {
            if (resource.isEmpty() || !resource.containsFluid(liquid)) {
                return false;
            }
        }

        Ingredient box = recipe.getBox();
        if (!box.hasNoMatchingItems() && !box.test(item)) {
            return false;
        }

        ShapedRecipe internal = recipe.getCraftingGridRecipe();
        return RecipeUtil.matches(internal, craftingInventory) != null;
    }

    public boolean isBox(RecipeManager manager, ItemStack resource) {
        if (resource.isEmpty()) {
            return false;
        }

        for (ICarpenterRecipe recipe : getRecipes(manager)) {
            Ingredient box = recipe.getBox();
            if (box.test(resource)) {
                return true;
            }
        }

        return false;
    }

    public Collection<ICarpenterRecipe> getRecipes(RecipeManager manager, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return Collections.emptyList();
        }

        return getRecipes(manager).stream().filter(recipe -> {
            ItemStack output = recipe.getCraftingGridRecipe().getRecipeOutput();
            return ItemStackUtil.isIdenticalItem(itemStack, output);
        }).collect(Collectors.toList());
    }

    public Set<Fluid> getRecipeFluids(RecipeManager manager) {
        if (recipeFluids.isEmpty()) {
            for (ICarpenterRecipe recipe : getRecipes(manager)) {
                FluidStack fluidStack = recipe.getFluidResource();
                if (!fluidStack.isEmpty()) {
                    recipeFluids.add(fluidStack.getFluid());
                }
            }
        }

        return Collections.unmodifiableSet(recipeFluids);
    }
}
