package forestry.factory.recipes.jei.still;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.core.render.ForestryResource;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeWrapper;

public class StillRecipeCategory extends ForestryRecipeCategory {
	
	private static final int inputTank = 0;
	private static final int outputTank = 1;
	
	private final static ResourceLocation guiTexture = new ForestryResource("textures/gui/still.png");
	
	@Nonnull
	private final IDrawable tankOverlay;
	@Nonnull
	private final IDrawableAnimated progressBar;
	
	public StillRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 34, 14, 108, 60), "tile.for.still.name");
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
		
		IDrawableStatic progressBarDrawable0 = guiHelper.createDrawable(guiTexture, 176, 74, 4, 18);
		this.progressBar = guiHelper.createAnimatedDrawable(progressBarDrawable0, 20, IDrawableAnimated.StartDirection.BOTTOM, false);
	}
	
	@Nonnull
	@Override
	public String getUid() {
		return ForestryRecipeCategoryUid.STILL;
	}

	@Override
	public void drawAnimations(@Nonnull Minecraft minecraft) {
		progressBar.draw(minecraft, 50, 3);
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiFluidStacks.init(inputTank, true, 1, 1, 16, 58, 10000, false, tankOverlay);
		guiFluidStacks.init(outputTank, false, 91, 1, 16, 58, 10000, false, tankOverlay);
		
		guiFluidStacks.set(inputTank, recipeWrapper.getFluidInputs());
		guiFluidStacks.set(outputTank, recipeWrapper.getFluidOutputs());
	}

}
