/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.recipes;

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
	@Nullable
	ISqueezerRecipe findMatchingRecipe(RecipeManager manager, NonNullList<ItemStack> items);

	boolean canUse(RecipeManager manager, ItemStack itemStack);
}
