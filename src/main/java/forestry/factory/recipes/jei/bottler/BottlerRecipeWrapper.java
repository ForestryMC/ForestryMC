package forestry.factory.recipes.jei.bottler;

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
	public void getIngredients(IIngredients ingredients) {
		BottlerRecipe recipe = getRecipe();

		ingredients.setInput(ItemStack.class, recipe.inputStack);
		if (recipe.outputStack != null) {
			ingredients.setOutput(ItemStack.class, recipe.outputStack);
		}

		if (recipe.fillRecipe) {
			ingredients.setInput(FluidStack.class, recipe.fluid);
		} else {
			ingredients.setOutput(FluidStack.class, recipe.fluid);
		}
	}
}
