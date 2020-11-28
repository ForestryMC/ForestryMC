package forestry.factory.recipes.jei.still;

import forestry.api.recipes.IStillRecipe;
import forestry.api.recipes.RecipeManagers;
import net.minecraft.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class StillRecipeMaker {
    public static List<StillRecipeWrapper> getStillRecipes(RecipeManager manager) {
        List<StillRecipeWrapper> recipes = new ArrayList<>();
        for (IStillRecipe recipe : RecipeManagers.stillManager.getRecipes(manager)) {
            recipes.add(new StillRecipeWrapper(recipe));
        }

        return recipes;
    }
}
