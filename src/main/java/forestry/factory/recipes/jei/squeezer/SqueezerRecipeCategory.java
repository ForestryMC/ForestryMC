package forestry.factory.recipes.jei.squeezer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.core.recipes.jei.ForestryTooltipCallback;
import forestry.core.render.ForestryResource;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;

public class SqueezerRecipeCategory extends ForestryRecipeCategory<AbstractSqueezerRecipeWrapper> {

	private static final int[][] INPUTS = new int[][]{{0, 0}, {1, 0}, {2, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {1, 2}, {2, 2}};

	private static final int craftOutputSlot = 0;
	private static final int craftInputSlot = 1;

	private static final int outputTank = 0;

	private static final ResourceLocation guiTexture = new ForestryResource("textures/gui/squeezersocket.png");

	private final IDrawableAnimated arrow;
	private final IDrawable tankOverlay;

	public SqueezerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 9, 16, 158, 62), "tile.for.squeezer.name");

		IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 60, 43, 18);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
	}

	@Override
	public String getUid() {
		return ForestryRecipeCategoryUid.SQUEEZER;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		arrow.draw(minecraft, 67, 25);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, AbstractSqueezerRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		for (int i = 0; i < INPUTS.length; i++) {
			guiItemStacks.init(craftInputSlot + i, true, 7 + INPUTS[i][0] * 18, 4 + INPUTS[i][1] * 18);
		}
		guiItemStacks.init(craftOutputSlot, false, 87, 43);
		guiFluidStacks.init(outputTank, false, 113, 2, 16, 58, 10000, false, tankOverlay);

		ForestryTooltipCallback tooltip = new ForestryTooltipCallback();
		float chance = recipeWrapper.getRemnantsChance();
		tooltip.addChanceTooltip(craftOutputSlot, chance);
		guiItemStacks.addTooltipCallback(tooltip);

		guiItemStacks.set(ingredients);
		guiFluidStacks.set(ingredients);
	}
}
