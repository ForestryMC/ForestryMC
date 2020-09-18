package forestry.factory.recipes.jei.centrifuge;

import forestry.api.recipes.ICentrifugeRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class CentrifugeRecipeWrapper extends ForestryRecipeWrapper<ICentrifugeRecipe> {
    public CentrifugeRecipeWrapper(ICentrifugeRecipe recipe) {
        super(recipe);
    }

    @Override
    public void setIngredients(IIngredients ingredients) {
        ICentrifugeRecipe recipe = getRecipe();

        ingredients.setInputs(VanillaTypes.ITEM, Collections.singletonList(recipe.getInput()));

        Set<ItemStack> outputs = recipe.getAllProducts().keySet();
        ingredients.setOutputs(VanillaTypes.ITEM, new ArrayList<>(outputs));
    }
}
