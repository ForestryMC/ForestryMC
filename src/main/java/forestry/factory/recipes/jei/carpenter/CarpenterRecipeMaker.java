package forestry.factory.recipes.jei.carpenter;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.RecipeManagers;
import net.minecraft.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class CarpenterRecipeMaker {
    public static List<CarpenterRecipeWrapper> getCarpenterRecipes(RecipeManager manager) {
        List<CarpenterRecipeWrapper> recipes = new ArrayList<>();
        for (ICarpenterRecipe recipe : RecipeManagers.carpenterManager.getRecipes(manager)) {
            recipes.add(new CarpenterRecipeWrapper(recipe));
        }

        return recipes;
    }
}
