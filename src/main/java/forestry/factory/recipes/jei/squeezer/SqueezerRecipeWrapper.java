package forestry.factory.recipes.jei.squeezer;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;

import forestry.api.recipes.ISqueezerRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;

public class SqueezerRecipeWrapper extends AbstractSqueezerRecipeWrapper<ISqueezerRecipe> {
	public SqueezerRecipeWrapper(ISqueezerRecipe recipe) {
		super(recipe);
	}

	@Override
	public void setIngredients(IIngredients ingredients) {
		NonNullList<Ingredient> resources = getRecipe().getResources();
		ingredients.setInputIngredients(resources);

		ItemStack remnants = getRecipe().getRemnants();
		if (!remnants.isEmpty()) {
			ingredients.setOutput(VanillaTypes.ITEM, remnants);
		}

		ingredients.setOutput(VanillaTypes.FLUID, getRecipe().getFluidOutput());
	}

	@Override
	public float getRemnantsChance() {
		return getRecipe().getRemnantsChance();
	}
}
