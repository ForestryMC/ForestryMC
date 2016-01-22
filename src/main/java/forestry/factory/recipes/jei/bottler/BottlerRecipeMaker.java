package forestry.factory.recipes.jei.bottler;

import java.util.ArrayList;
import java.util.List;

import forestry.factory.recipes.BottlerRecipe;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;

public class BottlerRecipeMaker {

	private BottlerRecipeMaker() {
	}
	
	public static List<BottlerRecipeWrapper> getBottlerRecipes() {
		List<BottlerRecipeWrapper> recipes = new ArrayList<>();
		for (FluidContainerData container : FluidContainerRegistry.getRegisteredFluidContainerData()) {
			BottlerRecipe recipe = BottlerRecipe.getRecipe(container.fluid, container.emptyContainer);
			if (recipe != null) {
				recipes.add(new BottlerRecipeWrapper(recipe));
			}
		}
		return recipes;
	}
	
}
