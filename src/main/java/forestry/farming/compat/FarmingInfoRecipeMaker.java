package forestry.farming.compat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.crafting.RecipeManager;

import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.ICircuit;
import forestry.api.farming.IFarmCircuit;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.api.recipes.ISolderRecipe;

import javax.annotation.Nullable;

public class FarmingInfoRecipeMaker {
	public static List<FarmingInfoRecipe> getRecipes(@Nullable RecipeManager manager) {
		List<FarmingInfoRecipe> recipes = new ArrayList<>();
		for (ISolderRecipe circuitRecipe : ChipsetManager.solderManager.getRecipes(manager)) {
			ICircuit circuit = circuitRecipe.getCircuit();
			if (circuit instanceof IFarmCircuit farmCircuit) {
				IFarmLogic logic = farmCircuit.getFarmLogic();
				if (logic.isManual()) {
					IFarmProperties properties = logic.getProperties();
					recipes.add(new FarmingInfoRecipe(circuitRecipe.getResource(), properties, circuit));
				}
			}
		}

		return recipes;
	}
}
