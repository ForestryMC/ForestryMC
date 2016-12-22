package forestry.factory.recipes.jei.still;

import java.util.ArrayList;
import java.util.List;

import forestry.api.recipes.IStillRecipe;
import forestry.api.recipes.RecipeManagers;

public class StillRecipeMaker {

	private StillRecipeMaker() {
	}

	public static List<StillRecipeWrapper> getStillRecipes() {
		List<StillRecipeWrapper> recipes = new ArrayList<>();
		for (IStillRecipe recipe : RecipeManagers.stillManager.recipes()) {
			recipes.add(new StillRecipeWrapper(recipe));
		}
		return recipes;
	}

}
