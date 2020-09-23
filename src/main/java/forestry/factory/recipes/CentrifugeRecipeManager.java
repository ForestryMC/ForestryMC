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
import forestry.api.recipes.IForestryRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CentrifugeRecipeManager implements ICentrifugeManager {

    private static final Set<ICentrifugeRecipe> recipes = new HashSet<>();

    @Override
    public void addRecipe(int timePerItem, ItemStack resource, Map<ItemStack, Float> products) {
        NonNullList<ICentrifugeRecipe.Product> list = NonNullList.create();

        for (Map.Entry<ItemStack, Float> entry : products.entrySet()) {
            list.add(new ICentrifugeRecipe.Product(entry.getValue(), entry.getKey()));
        }

        ICentrifugeRecipe recipe = new CentrifugeRecipe(
                IForestryRecipe.anonymous(),
                timePerItem,
                Ingredient.fromStacks(resource),
                list
        );
        addRecipe(recipe);
    }

    @Nullable
    public static ICentrifugeRecipe findMatchingRecipe(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return null;
        }

        for (ICentrifugeRecipe recipe : recipes) {
            Ingredient recipeInput = recipe.getInput();
            if (recipeInput.test(itemStack)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public boolean addRecipe(ICentrifugeRecipe recipe) {
        return recipes.add(recipe);
    }

    @Override
    public boolean removeRecipe(ICentrifugeRecipe recipe) {
        return recipes.remove(recipe);
    }

    @Override
    public Set<ICentrifugeRecipe> recipes() {
        return Collections.unmodifiableSet(recipes);
    }
}
