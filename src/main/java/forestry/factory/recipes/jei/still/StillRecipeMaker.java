package forestry.factory.recipes.jei.still;

import forestry.api.recipes.IStillRecipe;
import forestry.api.recipes.RecipeManagers;

import java.util.ArrayList;
import java.util.List;

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
