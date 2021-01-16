package forestry.factory.recipes.jei.still;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.crafting.RecipeManager;

import forestry.api.recipes.IStillRecipe;
import forestry.api.recipes.RecipeManagers;

public class StillRecipeMaker {
	public static List<StillRecipeWrapper> getStillRecipes(RecipeManager manager) {
		List<StillRecipeWrapper> recipes = new ArrayList<>();
		for (IStillRecipe recipe : RecipeManagers.stillManager.getRecipes(manager)) {
			recipes.add(new StillRecipeWrapper(recipe));
		}

		return recipes;
	}
}
