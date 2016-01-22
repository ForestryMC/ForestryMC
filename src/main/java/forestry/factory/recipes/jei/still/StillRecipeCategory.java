package forestry.factory.recipes.jei.still;

import javax.annotation.Nonnull;

import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class StillRecipeCategory extends ForestryRecipeCategory {
	
	private static final int inputTank = 0;
	private static final int outputTank = 1;
	
	private final static ResourceLocation guiTexture = new ResourceLocation("forestry", "textures/gui/still.png");
	
	@Nonnull
	protected final IDrawable tankOverlay;
	@Nonnull
	protected final IDrawableAnimated progressBar;
	
	public StillRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 30, 11, 116, 65), "tile.for.factory.6.name");
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
		
		IDrawableStatic progressBarDrawable0 = guiHelper.createDrawable(guiTexture, 176, 74, 4, 18);
		this.progressBar = guiHelper.createAnimatedDrawable(progressBarDrawable0, 20, IDrawableAnimated.StartDirection.BOTTOM, false);
	}
	
	@Override
	public String getUid() {
		return ForestryRecipeCategoryUid.STILL;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {	
	}

	@Override
	public void drawAnimations(Minecraft minecraft) {
		progressBar.draw(minecraft, 54, 6);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		
		
		guiFluidStacks.init(inputTank, true, 5, 4, 16, 58, 10000, false, tankOverlay);
		guiFluidStacks.init(outputTank, false, 95, 4, 16, 58, 10000, false, tankOverlay);
		
		guiFluidStacks.set(inputTank, recipeWrapper.getFluidInputs());
		guiFluidStacks.set(outputTank, recipeWrapper.getFluidOutputs());
		
	}

}
