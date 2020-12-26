package forestry.factory.recipes.jei.fermenter;

import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.utils.Log;
import mezz.jei.api.helpers.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FermenterRecipeMaker {
    public static List<FermenterRecipeWrapper> getFermenterRecipes(RecipeManager manager, IStackHelper stackHelper) {
        List<FermenterRecipeWrapper> recipes = new ArrayList<>();
        for (IFermenterRecipe recipe : RecipeManagers.fermenterManager.getRecipes(manager)) {
            if (recipe.getResource() != null) {
                addWrapperToList(stackHelper, recipe, recipe.getResource(), recipes);
            } else {
                Log.error("Empty resource for recipe");
            }
        }
        return recipes;
    }

    private static void addWrapperToList(
            IStackHelper stackHelper,
            IFermenterRecipe recipe,
            Ingredient resource,
            List<FermenterRecipeWrapper> recipes
    ) {
        Optional<ItemStack> itemStack = Arrays.stream(resource.getMatchingStacks()).findFirst();
        itemStack.ifPresent(stack -> recipes.add(new FermenterRecipeWrapper(recipe, stack)));
    }
}
