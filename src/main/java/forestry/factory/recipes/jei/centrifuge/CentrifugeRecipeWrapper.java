package forestry.factory.recipes.jei.centrifuge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import forestry.api.recipes.ICentrifugeRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;

public class CentrifugeRecipeWrapper extends ForestryRecipeWrapper<ICentrifugeRecipe> {
	public CentrifugeRecipeWrapper(ICentrifugeRecipe recipe) {
		super(recipe);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ICentrifugeRecipe recipe = getRecipe();

		ingredients.setInputs(ItemStack.class, Collections.singletonList(recipe.getInput()));

		Set<ItemStack> outputs = recipe.getAllProducts().keySet();
		ingredients.setOutputs(ItemStack.class, new ArrayList<>(outputs));
	}
}
