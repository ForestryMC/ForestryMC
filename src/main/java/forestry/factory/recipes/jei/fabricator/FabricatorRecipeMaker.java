package forestry.factory.recipes.jei.fabricator;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.crafting.RecipeManager;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.RecipeManagers;

public class FabricatorRecipeMaker {
	public static List<FabricatorRecipeWrapper> getFabricatorRecipes(RecipeManager manager) {
		List<FabricatorRecipeWrapper> recipes = new ArrayList<>();
		for (IFabricatorRecipe recipe : RecipeManagers.fabricatorManager.getRecipes(manager)) {
			recipes.add(new FabricatorRecipeWrapper(recipe));
		}

		return recipes;
	}
}
