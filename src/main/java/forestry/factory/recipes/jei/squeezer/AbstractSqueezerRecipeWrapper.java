package forestry.factory.recipes.jei.squeezer;

import javax.annotation.Nonnull;
import java.util.List;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import forestry.core.recipes.jei.ForestryRecipeWrapper;

public abstract class AbstractSqueezerRecipeWrapper<R> extends ForestryRecipeWrapper<R> {
	public AbstractSqueezerRecipeWrapper(@Nonnull R recipe) {
		super(recipe);
	}

	@Nonnull
	@Override
	public abstract List<ItemStack> getInputs();

	@Nonnull
	@Override
	public abstract List<FluidStack> getFluidOutputs();

	@Nonnull
	@Override
	public abstract List<ItemStack> getOutputs();

	/**
	 * @return Chance remnants will be produced by a single recipe cycle, from 0 to 1.
	 */
	public abstract float getRemnantsChance();
}
