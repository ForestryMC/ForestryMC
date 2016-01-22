package forestry.core.recipes.jei;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;

public abstract class ForestryRecipeWrapper<R> implements IRecipeWrapper {
	
	@Nonnull
	protected final R recipe;
	
	public ForestryRecipeWrapper(@Nonnull R recipe) {
		this.recipe = recipe;
	}
	
	@Override
	public List getInputs() {
		return Collections.emptyList();
	}
	
	@Override
	public List getOutputs() {
		return Collections.emptyList();
	}
	
	@Override
	public List<FluidStack> getFluidInputs() {
		return Collections.emptyList();
	}

	@Override
	public List<FluidStack> getFluidOutputs() {
		return Collections.emptyList();
	}

	@Override
	public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight) {
	}
	
	@Override
	public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
	}

	@Override
	public boolean handleClick(@Nonnull Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}

	@Override
	public void drawAnimations(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight) {
	}

	@Nonnull
	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return Collections.emptyList();
	}
	
	public R getRecipe() {
		return recipe;
	}

}
