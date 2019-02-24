package forestry.factory.recipes.jei.bottler;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;

public class BottlerRecipeWrapper implements IRecipeWrapper {
	public final ItemStack inputStack;
	public final FluidStack fluid;
	@Nullable
	public final ItemStack outputStack;
	public final boolean fillRecipe;

	public BottlerRecipeWrapper(ItemStack inputStack, FluidStack fluid, @Nullable ItemStack outputStack, boolean fillRecipe) {
		this.inputStack = inputStack;
		this.fluid = fluid;
		this.outputStack = outputStack;
		this.fillRecipe = fillRecipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.ITEM, inputStack);
		if (outputStack != null) {
			ingredients.setOutput(VanillaTypes.ITEM, outputStack);
		}

		if (fillRecipe) {
			ingredients.setInput(VanillaTypes.FLUID, fluid);
		} else {
			ingredients.setOutput(VanillaTypes.FLUID, fluid);
		}
	}
}
