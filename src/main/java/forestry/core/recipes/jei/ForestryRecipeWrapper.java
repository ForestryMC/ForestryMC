package forestry.core.recipes.jei;

//import mezz.jei.api.recipe.IRecipeWrapper;
//TODO - jei
public abstract class ForestryRecipeWrapper<R> {//implements IRecipeWrapper {
	private final R recipe;

	public ForestryRecipeWrapper(R recipe) {
		this.recipe = recipe;
	}

	public R getRecipe() {
		return recipe;
	}
}
