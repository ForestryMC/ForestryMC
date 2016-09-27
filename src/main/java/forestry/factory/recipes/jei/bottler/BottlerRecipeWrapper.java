package forestry.factory.recipes.jei.bottler;

import javax.annotation.Nonnull;
import java.util.Collections;

import forestry.core.recipes.jei.ForestryRecipeWrapper;
import forestry.factory.recipes.BottlerRecipe;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class BottlerRecipeWrapper extends ForestryRecipeWrapper<BottlerRecipe> {

	public BottlerRecipeWrapper(BottlerRecipe recipe) {
		super(recipe);
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		BottlerRecipe recipe = getRecipe();

		ingredients.setInputs(ItemStack.class, Collections.singletonList(recipe.empty));
		ingredients.setInputs(FluidStack.class, Collections.singletonList(recipe.input));

		ingredients.setOutput(ItemStack.class, recipe.filled);
	}
}
