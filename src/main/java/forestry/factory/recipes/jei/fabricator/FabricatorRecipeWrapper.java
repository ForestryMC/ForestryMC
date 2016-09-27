package forestry.factory.recipes.jei.fabricator;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import forestry.factory.recipes.jei.FactoryJeiPlugin;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FabricatorRecipeWrapper extends ForestryRecipeWrapper<IFabricatorRecipe> {
	
	public FabricatorRecipeWrapper(@Nonnull IFabricatorRecipe recipe) {
		super(recipe);
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		IFabricatorRecipe recipe = getRecipe();
		IStackHelper stackHelper = FactoryJeiPlugin.jeiHelpers.getStackHelper();

		Object[] itemInputs = recipe.getIngredients();
		List<List<ItemStack>> itemStackInputs = stackHelper.expandRecipeItemStackInputs(Arrays.asList(itemInputs));
		ingredients.setInputLists(ItemStack.class, itemStackInputs);

		ingredients.setInputs(FluidStack.class, Collections.singletonList(getRecipe().getLiquid()));

		ingredients.setOutput(ItemStack.class, recipe.getRecipeOutput());
	}
}
