package forestry.factory.recipes.jei.bottler;

import javax.annotation.Nonnull;

import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class BottlerRecipeCategory extends ForestryRecipeCategory{

	private static final int emptySlot = 0;
	private static final int outputSlot = 1;
	private static final int inputTank = 2;
	
	private final static ResourceLocation guiTexture = new ResourceLocation("forestry", "textures/gui/bottler.png");
	@Nonnull
	protected final IDrawableAnimated arrow;
	@Nonnull
	protected final IDrawable tankOverlay;
	
	public BottlerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 48, 11, 123, 65), "tile.for.factory.0.name");
		
		IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 74, 24, 17);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
	}

	@Override
	public String getUid() {
		return ForestryRecipeCategoryUid.BOTTLER;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		
	}

	@Override
	public void drawAnimations(Minecraft minecraft) {
		arrow.draw(minecraft, 32, 28);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		
		guiItemStacks.init(emptySlot, true, 67, 7);
		guiItemStacks.init(outputSlot, false, 67, 43);
		
		guiFluidStacks.init(inputTank, true, 5, 6, 16, 58, 10000, false, tankOverlay);
		
		guiItemStacks.set(emptySlot, recipeWrapper.getInputs());
		guiItemStacks.set(outputSlot, recipeWrapper.getOutputs());
		
		guiFluidStacks.set(inputTank, recipeWrapper.getFluidInputs());
	}

}
