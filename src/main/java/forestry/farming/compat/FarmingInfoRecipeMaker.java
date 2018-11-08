package forestry.farming.compat;

import java.util.ArrayList;
import java.util.List;

import forestry.api.circuits.ICircuit;
import forestry.api.farming.IFarmCircuit;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.core.circuits.CircuitRecipe;
import forestry.core.circuits.SolderManager;

public class FarmingInfoRecipeMaker {

	private FarmingInfoRecipeMaker() {
	}

	public static List<FarmingInfoRecipeWrapper> getRecipes() {
		List<FarmingInfoRecipeWrapper> recipes = new ArrayList<>();
		for (CircuitRecipe circuitRecipe : SolderManager.getRecipes()) {
			ICircuit circuit = circuitRecipe.getCircuit();
			if (circuit instanceof IFarmCircuit) {
				IFarmCircuit farmCircuit = (IFarmCircuit) circuit;
				IFarmLogic logic = farmCircuit.getFarmLogic();
				if (logic.isManual()) {
					IFarmProperties properties = logic.getProperties();
					recipes.add(new FarmingInfoRecipeWrapper(circuitRecipe.getResource(), properties, circuit));
				}
			}
		}
		return recipes;
	}
}
