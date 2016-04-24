package forestry.factory.recipes.jei.rainmaker;

import java.util.ArrayList;
import java.util.List;

import forestry.api.fuels.FuelManager;
import forestry.api.fuels.RainSubstrate;

public class RainmakerRecipeMaker {

	private RainmakerRecipeMaker() {
	}

	public static List<RainmakerRecipeWrapper> getRecipes() {
		List<RainmakerRecipeWrapper> recipes = new ArrayList<>();
		for (RainSubstrate substrate : FuelManager.rainSubstrate.values()) {
			recipes.add(new RainmakerRecipeWrapper(substrate));
		}
		return recipes;
	}
}
