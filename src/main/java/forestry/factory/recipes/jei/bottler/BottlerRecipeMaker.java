package forestry.factory.recipes.jei.bottler;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;

import forestry.factory.recipes.BottlerRecipe;

public class BottlerRecipeMaker {

	private BottlerRecipeMaker() {
	}
	
	public static List<BottlerRecipeWrapper> getBottlerRecipes() {
		List<BottlerRecipeWrapper> recipes = new ArrayList<>();
		for (FluidContainerData container : FluidContainerRegistry.getRegisteredFluidContainerData()) {
			FluidStack fluid = container.fluid;
			if (fluid != null) {
				BottlerRecipe recipe = BottlerRecipe.create(fluid.getFluid(), container.emptyContainer);
				if (recipe != null) {
					recipes.add(new BottlerRecipeWrapper(recipe));
				}
			}
		}
		return recipes;
	}
	
}
