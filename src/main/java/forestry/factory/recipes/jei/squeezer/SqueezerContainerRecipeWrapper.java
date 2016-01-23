package forestry.factory.recipes.jei.squeezer;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import forestry.core.recipes.jei.ForestryRecipeWrapper;
import forestry.factory.recipes.ISqueezerContainerRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

public class SqueezerContainerRecipeWrapper extends ForestryRecipeWrapper<ISqueezerContainerRecipe>{
	
	@Nonnull
	private ItemStack filledContainer;
	
	public SqueezerContainerRecipeWrapper(@Nonnull ISqueezerContainerRecipe recipe, @Nonnull ItemStack filledContainer) {
		super(recipe);
		this.filledContainer = filledContainer;
	}
	
	@Override
	public List getInputs() {
		return Collections.singletonList(filledContainer);
	}
	
	@Override
	public List<FluidStack> getFluidOutputs() {
		return Collections.singletonList(FluidContainerRegistry.getFluidForFilledItem(filledContainer));
	}

	@Override
	public List getOutputs() {
		return Collections.singletonList(recipe.getRemnants());
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

}
