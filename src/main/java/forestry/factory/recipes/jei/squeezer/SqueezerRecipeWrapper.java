package forestry.factory.recipes.jei.squeezer;

import javax.annotation.Nonnull;
import java.util.Arrays;

import forestry.api.recipes.ISqueezerRecipe;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class SqueezerRecipeWrapper extends AbstractSqueezerRecipeWrapper<ISqueezerRecipe> {
	
	public SqueezerRecipeWrapper(@Nonnull ISqueezerRecipe recipe) {
		super(recipe);
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		ItemStack[] resources = getRecipe().getResources();
		ingredients.setInputs(ItemStack.class, Arrays.asList(resources));

		ItemStack remnants = getRecipe().getRemnants();
		if (remnants != null) {
			ingredients.setOutput(ItemStack.class, remnants);
		}

		ingredients.setOutput(FluidStack.class, getRecipe().getFluidOutput());
	}

	@Override
	public float getRemnantsChance() {
		return getRecipe().getRemnantsChance();
	}
}
