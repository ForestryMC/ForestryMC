package forestry.factory.recipes.jei.carpenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;

public class CarpenterRecipeWrapper extends ForestryRecipeWrapper<ICarpenterRecipe> {
	public CarpenterRecipeWrapper(ICarpenterRecipe recipe) {
		super(recipe);
	}

	@Override
	public void setIngredients(IIngredients ingredients) {
		ICarpenterRecipe recipe = getRecipe();
		CraftingRecipe craftingGridRecipe = recipe.getCraftingGridRecipe();

		NonNullList<Ingredient> itemIngredients = recipe.getCraftingGridRecipe().getIngredients();
		List<Ingredient> inputStacks = new ArrayList<>();
		inputStacks.addAll(itemIngredients);

		inputStacks.add(recipe.getBox());

		ingredients.setInputIngredients(inputStacks);

		FluidStack fluidResource = recipe.getFluidResource();
		if (fluidResource != null) {
			ingredients.setInputs(VanillaTypes.FLUID, Collections.singletonList(fluidResource));
		}

		ItemStack recipeOutput = craftingGridRecipe.getResultItem();
		ingredients.setOutput(VanillaTypes.ITEM, recipeOutput);
	}
}
