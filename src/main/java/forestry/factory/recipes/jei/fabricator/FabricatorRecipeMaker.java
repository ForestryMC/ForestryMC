package forestry.factory.recipes.jei.fabricator;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.RecipeManagers;

import java.util.ArrayList;
import java.util.List;

public class FabricatorRecipeMaker {

    private FabricatorRecipeMaker() {
    }

    public static List<FabricatorRecipeWrapper> getFabricatorRecipes() {
        List<FabricatorRecipeWrapper> recipes = new ArrayList<>();
        for (IFabricatorRecipe recipe : RecipeManagers.fabricatorManager.recipes()) {
            recipes.add(new FabricatorRecipeWrapper(recipe));
        }
        return recipes;
    }
}
