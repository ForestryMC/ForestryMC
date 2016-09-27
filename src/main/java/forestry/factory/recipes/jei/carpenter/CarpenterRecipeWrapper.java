package forestry.factory.recipes.jei.carpenter;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import forestry.factory.recipes.jei.FactoryJeiPlugin;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class CarpenterRecipeWrapper extends ForestryRecipeWrapper<ICarpenterRecipe> {
	
	public CarpenterRecipeWrapper(@Nonnull ICarpenterRecipe recipe) {
		super(recipe);
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		ICarpenterRecipe recipe = getRecipe();
		IDescriptiveRecipe craftingGridRecipe = recipe.getCraftingGridRecipe();
		Object[] inputs = craftingGridRecipe.getIngredients();
		IStackHelper stackHelper = FactoryJeiPlugin.jeiHelpers.getStackHelper();

		List<List<ItemStack>> inputStacks = stackHelper.expandRecipeItemStackInputs(Arrays.asList(inputs));
		ItemStack box = recipe.getBox();
		if (box != null) {
			inputStacks.add(Collections.singletonList(box));
		}
		ingredients.setInputLists(ItemStack.class, inputStacks);

		FluidStack fluidResource = recipe.getFluidResource();
		if (fluidResource != null) {
			ingredients.setInputs(FluidStack.class, Collections.singletonList(fluidResource));
		}

		ItemStack recipeOutput = craftingGridRecipe.getRecipeOutput();
		ingredients.setOutput(ItemStack.class, recipeOutput);
	}
}
