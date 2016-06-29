package forestry.factory.recipes.jei.fermenter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IVariableFermentable;
import forestry.api.recipes.RecipeManagers;
import forestry.core.utils.Log;
import mezz.jei.api.recipe.IStackHelper;

public class FermenterRecipeMaker {

	private FermenterRecipeMaker() {
	}
	
	public static List<FermenterRecipeWrapper> getFermenterRecipes(IStackHelper stackHelper) {
		List<FermenterRecipeWrapper> recipes = new ArrayList<>();
		for (IFermenterRecipe recipe : RecipeManagers.fermenterManager.recipes()) {
			if (recipe.getResource() != null) {
				if (recipe.getResource().getItem() instanceof IVariableFermentable) {
					for (ItemStack stack : stackHelper.getSubtypes(recipe.getResource())) {
						recipes.add(new FermenterRecipeWrapper(recipe, stack));
					}
				} else {
					recipes.add(new FermenterRecipeWrapper(recipe, recipe.getResource()));
				}
			} else {
				Log.error("Null resource for recipe");
			}
		}
		return recipes;
	}
	
}
