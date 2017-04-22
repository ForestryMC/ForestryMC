package forestry.factory.recipes.jei.squeezer;

import java.util.ArrayList;
import java.util.List;

import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.recipes.ISqueezerContainerRecipe;
import forestry.factory.recipes.SqueezerRecipeManager;
import mezz.jei.api.ingredients.IIngredientRegistry;
import net.minecraft.item.ItemStack;

public class SqueezerRecipeMaker {

	private SqueezerRecipeMaker() {
	}

	public static List<SqueezerRecipeWrapper> getSqueezerRecipes() {
		List<SqueezerRecipeWrapper> recipes = new ArrayList<>();
		for (ISqueezerRecipe recipe : RecipeManagers.squeezerManager.recipes()) {
			recipes.add(new SqueezerRecipeWrapper(recipe));
		}
		return recipes;
	}

	public static List<SqueezerContainerRecipeWrapper> getSqueezerContainerRecipes(IIngredientRegistry ingredientRegistry) {
		List<SqueezerContainerRecipeWrapper> recipes = new ArrayList<>();
		for (ItemStack stack : ingredientRegistry.getIngredients(ItemStack.class)) {
			ISqueezerContainerRecipe containerRecipe = SqueezerRecipeManager.findMatchingContainerRecipe(stack);
			if (containerRecipe != null) {
				recipes.add(new SqueezerContainerRecipeWrapper(containerRecipe, stack));
			}
		}
		return recipes;
	}
}
