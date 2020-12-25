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

import forestry.api.recipes.IFabricatorSmeltingManager;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FabricatorSmeltingRecipeManager extends AbstractCraftingProvider<IFabricatorSmeltingRecipe> implements IFabricatorSmeltingManager {
    private final Set<Fluid> recipeFluids = new HashSet<>();

    public FabricatorSmeltingRecipeManager() {
        super(IFabricatorSmeltingRecipe.TYPE);
    }

    @Nullable
    public IFabricatorSmeltingRecipe findMatchingSmelting(RecipeManager manager, ItemStack resource) {
        if (resource.isEmpty()) {
            return null;
        }

        for (IFabricatorSmeltingRecipe smelting : getRecipes(manager)) {
            if (smelting.getResource().test(resource)) {
                return smelting;
            }
        }

        return null;
    }

    public Set<Fluid> getRecipeFluids(RecipeManager manager) {
        if (recipeFluids.isEmpty()) {
            for (IFabricatorSmeltingRecipe recipe : getRecipes(manager)) {
                FluidStack fluidStack = recipe.getProduct();
                if (!fluidStack.isEmpty()) {
                    recipeFluids.add(fluidStack.getFluid());
                }
            }
        }
        return Collections.unmodifiableSet(recipeFluids);
    }
}
