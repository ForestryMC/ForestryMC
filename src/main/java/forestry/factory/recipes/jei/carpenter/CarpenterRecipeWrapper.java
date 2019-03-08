package forestry.factory.recipes.jei.carpenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;

public class CarpenterRecipeWrapper extends ForestryRecipeWrapper<ICarpenterRecipe> {
	private final List<List<ItemStack>> inputStacks;

	public CarpenterRecipeWrapper(ICarpenterRecipe recipe) {
		super(recipe);

		IDescriptiveRecipe craftingGridRecipe = recipe.getCraftingGridRecipe();
		NonNullList<NonNullList<ItemStack>> inputs = craftingGridRecipe.getRawIngredients();

		this.inputStacks = new ArrayList<>();
		for (List<ItemStack> stacks : inputs) {
			List<ItemStack> copy = new ArrayList<>();
			copy.addAll(stacks);
			this.inputStacks.add(copy);
		}
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ICarpenterRecipe recipe = getRecipe();
		IDescriptiveRecipe craftingGridRecipe = recipe.getCraftingGridRecipe();

		List<List<ItemStack>> inputStacks = new ArrayList<>();
		ItemStack box = recipe.getBox();
		if (!box.isEmpty()) {
			inputStacks.add(Collections.singletonList(box));
		}

		inputStacks.addAll(getInputStacks());

		ingredients.setInputLists(VanillaTypes.ITEM, inputStacks);

		FluidStack fluidResource = recipe.getFluidResource();
		if (fluidResource != null) {
			ingredients.setInputs(VanillaTypes.FLUID, Collections.singletonList(fluidResource));
		}

		ItemStack recipeOutput = craftingGridRecipe.getOutput();
		ingredients.setOutput(VanillaTypes.ITEM, recipeOutput);
	}

	public List<List<ItemStack>> getInputStacks() {
		return inputStacks;
	}
}
