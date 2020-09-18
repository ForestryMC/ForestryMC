package forestry.factory.recipes.jei.still;

import forestry.api.recipes.IStillRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;

import java.util.Collections;

public class StillRecipeWrapper extends ForestryRecipeWrapper<IStillRecipe> {

    public StillRecipeWrapper(IStillRecipe recipe) {
        super(recipe);
    }

    @Override
    public void setIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.FLUID, Collections.singletonList(getRecipe().getInput()));
        ingredients.setOutput(VanillaTypes.FLUID, getRecipe().getOutput());
    }
}
