/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.recipes;

import forestry.factory.recipes.ISqueezerContainerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

/**
 * Provides an interface to the recipe manager of the suqeezer.
 * <p>
 * The manager is initialized at the beginning of Forestry's BaseMod.load() cycle. Begin adding recipes in BaseMod.ModsLoaded() and this shouldn't be null even
 * if your mod loads before Forestry.
 * <p>
 * Accessible via {@link RecipeManagers}
 *
 * @author SirSengir
 */
public interface ISqueezerManager extends ICraftingProvider<ISqueezerRecipe> {
    /**
     * Add a recipe for a fluid container to the squeezer.
     * This will add recipes to get all types of liquids out of this type of fluid container.
     *
     * @param timePerItem    Number of work cycles required to squeeze one set of resources.
     * @param emptyContainer The empty version of the fluid container that will be squeezed.
     * @param remnants       Item stack representing the possible remnants from this recipe. May be empty.
     * @param chance         Chance remnants will be produced by a single recipe cycle, from 0 to 1.
     */
    void addContainerRecipe(int timePerItem, ItemStack emptyContainer, ItemStack remnants, float chance);

    @Nullable
    ISqueezerRecipe findMatchingRecipe(RecipeManager manager, NonNullList<ItemStack> items);

    @Nullable
    public ISqueezerContainerRecipe findMatchingContainerRecipe(ItemStack filledContainer);

    public boolean canUse(RecipeManager manager, ItemStack itemStack);
}
