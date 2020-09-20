package forestry.factory.recipes.jei.moistener;

import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.api.recipes.RecipeManagers;

import java.util.ArrayList;
import java.util.List;

public class MoistenerRecipeMaker {
    public static List<MoistenerRecipeWrapper> getMoistenerRecipes() {
        List<MoistenerRecipeWrapper> recipes = new ArrayList<>();
        for (IMoistenerRecipe recipe : RecipeManagers.moistenerManager.recipes()) {
            for (MoistenerFuel fuel : FuelManager.moistenerResource.values()) {
                recipes.add(new MoistenerRecipeWrapper(recipe, fuel));
            }
        }
        return recipes;
    }

}
