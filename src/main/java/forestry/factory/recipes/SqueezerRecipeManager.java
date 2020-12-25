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

import forestry.api.recipes.ISqueezerManager;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.core.fluids.FluidHelper;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.datastructures.ItemStackMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

public class SqueezerRecipeManager extends AbstractCraftingProvider<ISqueezerRecipe> implements ISqueezerManager {
    private final ItemStackMap<ISqueezerContainerRecipe> containerRecipes = new ItemStackMap<>();

    public SqueezerRecipeManager() {
        super(ISqueezerRecipe.TYPE);
    }

    @Override
    public void addContainerRecipe(int timePerItem, ItemStack emptyContainer, ItemStack remnants, float chance) {
        containerRecipes.put(
                emptyContainer,
                new SqueezerContainerRecipe(emptyContainer, timePerItem, remnants, chance)
        );
    }

    @Nullable
    public ISqueezerContainerRecipe findMatchingContainerRecipe(ItemStack filledContainer) {
        if (!FluidHelper.isDrainableFilledContainer(filledContainer)) {
            return null;
        }

        return containerRecipes.get(new ItemStack(filledContainer.getItem()));
    }

    @Nullable
    public ISqueezerRecipe findMatchingRecipe(RecipeManager manager, NonNullList<ItemStack> items) {
        // Find container recipes
        for (ItemStack itemStack : items) {
            ISqueezerContainerRecipe containerRecipe = findMatchingContainerRecipe(itemStack);
            if (containerRecipe != null) {
                ISqueezerRecipe recipe = containerRecipe.getSqueezerRecipe(itemStack);
                if (recipe != null) {
                    return recipe;
                }
            }
        }

        for (ISqueezerRecipe recipe : getRecipes(manager)) {
            if (ItemStackUtil.containsSets(recipe.getResources(), items, false) > 0) {
                return recipe;
            }
        }

        return null;
    }

    public boolean canUse(RecipeManager manager, ItemStack itemStack) {
        for (ISqueezerRecipe recipe : getRecipes(manager)) {
            for (ItemStack recipeInput : recipe.getResources()) {
                if (ItemStackUtil.isCraftingEquivalent(recipeInput, itemStack, false)) {
                    return true;
                }
            }
        }

        return findMatchingContainerRecipe(itemStack) != null;
    }
}
