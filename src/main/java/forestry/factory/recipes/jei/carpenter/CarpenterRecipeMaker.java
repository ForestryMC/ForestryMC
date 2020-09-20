package forestry.factory.recipes.jei.carpenter;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.RecipeManagers;

import java.util.ArrayList;
import java.util.List;

public class CarpenterRecipeMaker {
    public static List<CarpenterRecipeWrapper> getCarpenterRecipes() {
        List<CarpenterRecipeWrapper> recipes = new ArrayList<>();
        for (ICarpenterRecipe recipe : RecipeManagers.carpenterManager.recipes()) {
            recipes.add(new CarpenterRecipeWrapper(recipe));
        }

        return recipes;
    }
}
