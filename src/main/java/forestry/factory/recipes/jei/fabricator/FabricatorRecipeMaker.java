package forestry.factory.recipes.jei.fabricator;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.RecipeManagers;
import net.minecraft.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class FabricatorRecipeMaker {
    public static List<FabricatorRecipeWrapper> getFabricatorRecipes(RecipeManager manager) {
        List<FabricatorRecipeWrapper> recipes = new ArrayList<>();
        for (IFabricatorRecipe recipe : RecipeManagers.fabricatorManager.getRecipes(manager)) {
            recipes.add(new FabricatorRecipeWrapper(recipe));
        }

        return recipes;
    }
}
