package forestry.factory.recipes.jei.fermenter;

import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.RecipeManagers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FermenterRecipeMaker {
	public static List<FermenterRecipeWrapper> getFermenterRecipes(@Nullable RecipeManager manager) {
		List<FermenterRecipeWrapper> recipes = new ArrayList<>();
		for (IFermenterRecipe recipe : RecipeManagers.fermenterManager.getRecipes(manager)) {
			for (ItemStack stack : recipe.getResource().getItems()) {
				recipes.add(new FermenterRecipeWrapper(recipe, stack));
			}
		}
		return recipes;
	}

}
