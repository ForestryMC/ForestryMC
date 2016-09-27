package forestry.factory.recipes.jei.squeezer;

import javax.annotation.Nonnull;
import java.util.Collections;

import forestry.factory.recipes.ISqueezerContainerRecipe;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class SqueezerContainerRecipeWrapper extends AbstractSqueezerRecipeWrapper<ISqueezerContainerRecipe> {
	
	@Nonnull
	private final ItemStack filledContainer;
	
	public SqueezerContainerRecipeWrapper(@Nonnull ISqueezerContainerRecipe recipe, @Nonnull ItemStack filledContainer) {
		super(recipe);
		this.filledContainer = filledContainer;
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		ingredients.setInputs(ItemStack.class, Collections.singletonList(filledContainer));

		ItemStack remnants = getRecipe().getRemnants();
		if (remnants != null) {
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
