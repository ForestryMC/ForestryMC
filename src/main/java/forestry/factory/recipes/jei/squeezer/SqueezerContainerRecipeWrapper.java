package forestry.factory.recipes.jei.squeezer;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.factory.recipes.ISqueezerContainerRecipe;

public class SqueezerContainerRecipeWrapper extends AbstractSqueezerRecipeWrapper<ISqueezerContainerRecipe> {
	
	@Nonnull
	private final ItemStack filledContainer;
	
	public SqueezerContainerRecipeWrapper(@Nonnull ISqueezerContainerRecipe recipe, @Nonnull ItemStack filledContainer) {
		super(recipe);
		this.filledContainer = filledContainer;
	}

	@Nonnull
	@Override
	public List<ItemStack> getInputs() {
		return Collections.singletonList(filledContainer);
	}

	@Nonnull
	@Override
	public List<FluidStack> getFluidOutputs() {
		return Collections.singletonList(FluidContainerRegistry.getFluidForFilledItem(filledContainer));
	}

	@Nonnull
	@Override
	public List<ItemStack> getOutputs() {
		return Collections.singletonList(getRecipe().getRemnants());
	}

	@Override
	public float getRemnantsChance() {
		return getRecipe().getRemnantsChance();
	}
}
