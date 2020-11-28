package forestry.factory.recipes.jei.centrifuge;

import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.RecipeManagers;
import net.minecraft.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class CentrifugeRecipeMaker {
    public static List<CentrifugeRecipeWrapper> getCentrifugeRecipe(RecipeManager manager) {
        List<CentrifugeRecipeWrapper> recipes = new ArrayList<>();
        for (ICentrifugeRecipe recipe : RecipeManagers.centrifugeManager.getRecipes(manager)) {
            recipes.add(new CentrifugeRecipeWrapper(recipe));
        }

        return recipes;
    }
}
