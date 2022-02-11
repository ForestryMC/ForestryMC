package forestry.factory.recipes.jei.moistener;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.crafting.RecipeManager;

import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.api.recipes.RecipeManagers;
import org.jetbrains.annotations.Nullable;

public class MoistenerRecipeMaker {
	public static List<MoistenerRecipe> getMoistenerRecipes(@Nullable RecipeManager manager) {
		List<MoistenerRecipe> recipes = new ArrayList<>();
		for (IMoistenerRecipe recipe : RecipeManagers.moistenerManager.getRecipes(manager)) {
			for (MoistenerFuel fuel : FuelManager.moistenerResource.values()) {
				recipes.add(new MoistenerRecipe(recipe, fuel));
			}
		}

		return recipes;
	}

}
