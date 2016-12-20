package forestry.factory.recipes.jei.squeezer;

import forestry.api.recipes.ISqueezerRecipe;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

public class SqueezerRecipeWrapper extends AbstractSqueezerRecipeWrapper<ISqueezerRecipe> {
	public SqueezerRecipeWrapper(ISqueezerRecipe recipe) {
		super(recipe);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		NonNullList<ItemStack> resources = getRecipe().getResources();
		ingredients.setInputs(ItemStack.class, resources);

		ItemStack remnants = getRecipe().getRemnants();
		if (!remnants.isEmpty()) {
			ingredients.setOutput(ItemStack.class, remnants);
		}

		ingredients.setOutput(FluidStack.class, getRecipe().getFluidOutput());
	}

	@Override
	public float getRemnantsChance() {
		return getRecipe().getRemnantsChance();
	}
}
