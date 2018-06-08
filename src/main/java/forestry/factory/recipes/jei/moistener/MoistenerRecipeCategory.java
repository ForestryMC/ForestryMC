package forestry.factory.recipes.jei.moistener;

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
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;

public class MoistenerRecipeCategory extends ForestryRecipeCategory<MoistenerRecipeWrapper> {

	private static final int resourceSlot = 0;
	private static final int productSlot = 1;
	private static final int fuelItemSlot = 2;
	private static final int fuelProductSlot = 3;

	private static final int inputTank = 0;

	private static final ResourceLocation guiTexture = new ForestryResource("textures/gui/moistener.png");

	private final IDrawableAnimated arrow;
	private final IDrawableAnimated progressBar;
	private final IDrawable tankOverlay;

	public MoistenerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 15, 15, 145, 60), "tile.for.moistener.name");

		IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 91, 29, 55);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 80, IDrawableAnimated.StartDirection.BOTTOM, false);
		IDrawableStatic progressBar = guiHelper.createDrawable(guiTexture, 176, 74, 16, 15);
		this.progressBar = guiHelper.createAnimatedDrawable(progressBar, 160, IDrawableAnimated.StartDirection.LEFT, false);
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
	}

	@Override
	public String getUid() {
		return ForestryRecipeCategoryUid.MOISTENER;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		arrow.draw(minecraft, 78, 2);
		progressBar.draw(minecraft, 109, 22);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, MoistenerRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		guiItemStacks.init(resourceSlot, true, 127, 3);
		guiItemStacks.init(fuelItemSlot, true, 23, 42);

		guiItemStacks.init(productSlot, false, 127, 39);
		guiItemStacks.init(fuelProductSlot, false, 89, 21);

		guiFluidStacks.init(inputTank, true, 1, 1, 16, 58, 10000, false, tankOverlay);

		guiItemStacks.set(ingredients);
		guiFluidStacks.set(ingredients);
	}

}
