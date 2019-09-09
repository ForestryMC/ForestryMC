//package forestry.factory.recipes.jei.centrifuge;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import forestry.api.recipes.ICentrifugeRecipe;
//import forestry.api.recipes.RecipeManagers;
//
//public class CentrifugeRecipeMaker {
//
//	private CentrifugeRecipeMaker() {
//	}
//
//	public static List<CentrifugeRecipeWrapper> getCentrifugeRecipe() {
//		List<CentrifugeRecipeWrapper> recipes = new ArrayList<>();
//		for (ICentrifugeRecipe recipe : RecipeManagers.centrifugeManager.recipes()) {
//			recipes.add(new CentrifugeRecipeWrapper(recipe));
//		}
//		return recipes;
//	}
//
//}
