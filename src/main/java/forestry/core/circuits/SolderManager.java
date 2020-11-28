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
package forestry.core.circuits;

import com.google.common.base.Preconditions;
import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.recipes.IForestryRecipe;
import forestry.api.recipes.ISolderManager;
import forestry.api.recipes.ISolderRecipe;
import forestry.factory.recipes.AbstractCraftingProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;

import javax.annotation.Nullable;

public class SolderManager extends AbstractCraftingProvider<ISolderRecipe> implements ISolderManager {
    public SolderManager() {
        super(ISolderRecipe.TYPE);
    }

    @Nullable
    public ICircuit getCircuit(RecipeManager manager, ICircuitLayout layout, ItemStack resource) {
        ISolderRecipe circuitRecipe = getMatchingRecipe(manager, layout, resource);
        if (circuitRecipe == null) {
            return null;
        }

        return circuitRecipe.getCircuit();
    }

    @Nullable
    public ISolderRecipe getMatchingRecipe(
            RecipeManager manager,
            @Nullable ICircuitLayout layout,
            ItemStack resource
    ) {
        if (layout != null) {
            for (ISolderRecipe recipe : getRecipes(manager)) {
                if (recipe.matches(layout, resource)) {
                    return recipe;
                }
            }
        }
        return null;
    }
}
