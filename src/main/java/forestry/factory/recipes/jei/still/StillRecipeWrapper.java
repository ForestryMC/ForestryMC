//package forestry.factory.recipes.jei.still;
//
//import java.util.Collections;
//
//import forestry.api.recipes.IStillRecipe;
//import forestry.core.recipes.jei.ForestryRecipeWrapper;
//
//import mezz.jei.api.ingredients.IIngredients;
//import mezz.jei.api.ingredients.VanillaTypes;
//
//public class StillRecipeWrapper extends ForestryRecipeWrapper<IStillRecipe> {
//
//	public StillRecipeWrapper(IStillRecipe recipe) {
//		super(recipe);
//	}
//
//	@Override
//	public void getIngredients(IIngredients ingredients) {
//		ingredients.setInputs(VanillaTypes.FLUID, Collections.singletonList(getRecipe().getInput()));
//		ingredients.setOutput(VanillaTypes.FLUID, getRecipe().getOutput());
//	}
//}
