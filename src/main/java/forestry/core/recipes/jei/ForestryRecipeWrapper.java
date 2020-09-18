package forestry.core.recipes.jei;

import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;

public abstract class ForestryRecipeWrapper<R> implements IRecipeCategoryExtension {
    private final R recipe;

    public ForestryRecipeWrapper(R recipe) {
        this.recipe = recipe;
    }

    public R getRecipe() {
        return recipe;
    }
}
