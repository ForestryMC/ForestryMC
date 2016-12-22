package forestry.factory.recipes.jei.carpenter;

import java.util.ArrayList;
import java.util.List;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.RecipeManagers;

public class CarpenterRecipeMaker {

	private CarpenterRecipeMaker() {
	}

	public static List<CarpenterRecipeWrapper> getCarpenterRecipes() {
		List<CarpenterRecipeWrapper> recipes = new ArrayList<>();
		for (ICarpenterRecipe recipe : RecipeManagers.carpenterManager.recipes()) {
			recipes.add(new CarpenterRecipeWrapper(recipe));
		}
		return recipes;
	}

}
