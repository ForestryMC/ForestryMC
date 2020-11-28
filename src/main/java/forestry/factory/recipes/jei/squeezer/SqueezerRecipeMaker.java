package forestry.factory.recipes.jei.squeezer;

import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.recipes.ISqueezerContainerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.item.ItemStack;
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

    public static List<SqueezerContainerRecipeWrapper> getSqueezerContainerRecipes(IIngredientManager ingredientRegistry) {
        List<SqueezerContainerRecipeWrapper> recipes = new ArrayList<>();
        for (ItemStack stack : ingredientRegistry.getAllIngredients(VanillaTypes.ITEM)) {
            ISqueezerContainerRecipe containerRecipe = RecipeManagers.squeezerManager.findMatchingContainerRecipe(stack);
            if (containerRecipe != null) {
                recipes.add(new SqueezerContainerRecipeWrapper(containerRecipe, stack));
            }
        }

        return recipes;
    }
}
