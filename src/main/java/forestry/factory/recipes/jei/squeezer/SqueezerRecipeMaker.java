package forestry.factory.recipes.jei.squeezer;

import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.RecipeManagers;
import net.minecraft.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class SqueezerRecipeMaker {
    public static List<SqueezerRecipeWrapper> getSqueezerRecipes(RecipeManager manager) {
        List<SqueezerRecipeWrapper> recipes = new ArrayList<>();
        for (ISqueezerRecipe recipe : RecipeManagers.squeezerManager.getRecipes(manager)) {
            recipes.add(new SqueezerRecipeWrapper(recipe));
        }

        return recipes;
    }
}
