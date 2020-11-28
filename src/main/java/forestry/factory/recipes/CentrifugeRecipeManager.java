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

import forestry.api.recipes.ICentrifugeManager;
import forestry.api.recipes.ICentrifugeRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;

import javax.annotation.Nullable;

public class CentrifugeRecipeManager extends AbstractCraftingProvider<ICentrifugeRecipe> implements ICentrifugeManager {
    public CentrifugeRecipeManager() {
        super(ICentrifugeRecipe.TYPE);
    }

    @Nullable
    public ICentrifugeRecipe findMatchingRecipe(RecipeManager manager, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return null;
        }

        for (ICentrifugeRecipe recipe : getRecipes(manager)) {
            Ingredient recipeInput = recipe.getInput();
            if (recipeInput.test(itemStack)) {
                return recipe;
            }
        }
        return null;
    }
}
