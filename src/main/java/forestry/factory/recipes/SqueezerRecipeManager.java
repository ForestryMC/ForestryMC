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
import forestry.core.utils.ItemStackUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

public class SqueezerRecipeManager extends AbstractCraftingProvider<ISqueezerRecipe> implements ISqueezerManager {
    public SqueezerRecipeManager() {
        super(ISqueezerRecipe.TYPE);
    }

    @Nullable
    public ISqueezerRecipe findMatchingRecipe(RecipeManager manager, NonNullList<ItemStack> items) {
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
                if (ItemStackUtil.isCraftingEquivalent(recipeInput, itemStack, false)
                    || recipe.isFilledContainer(itemStack)
                ) {
                    return true;
                }
            }
        }

        return false;
    }
}
