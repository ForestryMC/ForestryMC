package forestry.factory.recipes.jei.squeezer;

import java.util.Collections;

import forestry.factory.recipes.ISqueezerContainerRecipe;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class SqueezerContainerRecipeWrapper extends AbstractSqueezerRecipeWrapper<ISqueezerContainerRecipe> {
	private final ItemStack filledContainer;

	public SqueezerContainerRecipeWrapper(ISqueezerContainerRecipe recipe, ItemStack filledContainer) {
		super(recipe);
		this.filledContainer = filledContainer;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputs(ItemStack.class, Collections.singletonList(filledContainer));

		ItemStack remnants = getRecipe().getRemnants();
		if (!remnants.isEmpty()) {
			ingredients.setOutput(ItemStack.class, remnants);
		}

		FluidStack fluidContained = FluidUtil.getFluidContained(filledContainer);
		if (fluidContained != null) {
			ingredients.setOutput(FluidStack.class, fluidContained);
		}
	}

	@Override
	public float getRemnantsChance() {
		return getRecipe().getRemnantsChance();
	}
}
