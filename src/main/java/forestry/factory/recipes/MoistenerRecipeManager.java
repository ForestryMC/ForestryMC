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

import forestry.api.recipes.IMoistenerManager;
import forestry.api.recipes.IMoistenerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class MoistenerRecipeManager extends AbstractCraftingProvider<IMoistenerRecipe> implements IMoistenerManager {
    private final Set<IMoistenerRecipe> recipes = new HashSet<>();

    public MoistenerRecipeManager() {
        super(IMoistenerRecipe.TYPE);
    }

    public boolean isResource(RecipeManager manager, ItemStack resource) {
        if (resource.isEmpty()) {
            return false;
        }

        for (IMoistenerRecipe rec : getRecipes(manager)) {
            if (rec.getResource().test(resource)) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    public IMoistenerRecipe findMatchingRecipe(RecipeManager manager, ItemStack item) {
        for (IMoistenerRecipe recipe : getRecipes(manager)) {
            if (recipe.getResource().test(item)) {
                return recipe;
            }
        }

        return null;
    }
}
