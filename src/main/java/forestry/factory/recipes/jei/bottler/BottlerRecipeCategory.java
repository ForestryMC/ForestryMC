package forestry.factory.recipes.jei.bottler;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.core.render.ForestryResource;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;

public class BottlerRecipeCategory extends ForestryRecipeCategory<BottlerRecipeWrapper> {

	private static final int inputFull = 0;
	private static final int outputEmpty = 1;
	private static final int inputEmpty = 2;
	private static final int outputFull = 3;
	private static final int tankIndex = 0;

	private final static ResourceLocation guiTexture = new ForestryResource("textures/gui/bottler.png");

	private final IDrawable slot;
	private final IDrawable tank;
	private final IDrawable arrowDown;
	private final IDrawable tankOverlay;

	public BottlerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createBlankDrawable(62, 60), "tile.for.bottler.name");

		this.slot = guiHelper.getSlotDrawable();
		this.tank = guiHelper.createDrawable(guiTexture, 79, 13, 18, 60);
		this.arrowDown = guiHelper.createDrawable(guiTexture, 20, 25, 12, 8);
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
	}

	@Override
	public String getUid() {
		return ForestryRecipeCategoryUid.BOTTLER;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		slot.draw(minecraft, 0, 0);
		arrowDown.draw(minecraft, 3, 26);
		slot.draw(minecraft, 0, 42);

		tank.draw(minecraft, 22, 0);

		slot.draw(minecraft, 44, 0);
		arrowDown.draw(minecraft, 47, 26);
		slot.draw(minecraft, 44, 42);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, BottlerRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		if (recipeWrapper.fillRecipe) {
			guiItemStacks.init(inputEmpty, true, 44, 0);
			guiItemStacks.init(outputFull, false, 44, 42);
			guiFluidStacks.init(tankIndex, true, 23, 1, 16, 58, 10000, false, tankOverlay);
		} else {
			guiItemStacks.init(inputFull, true, 0, 0);
			guiItemStacks.init(outputEmpty, false, 0, 42);
			guiFluidStacks.init(tankIndex, false, 23, 1, 16, 58, 10000, false, tankOverlay);
		}

		guiItemStacks.set(ingredients);
		guiFluidStacks.set(ingredients);
	}

}
