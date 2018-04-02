package forestry.book.gui.elements;

import javax.annotation.Nullable;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import forestry.api.gui.GuiElementAlignment;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.ButtonElement;
import forestry.core.gui.elements.IngredientElement;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.core.gui.elements.layouts.PaneLayout;
import forestry.core.utils.Translator;

public class CraftingElement extends PaneLayout {
	private static final ResourceLocation BOOK_TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/atlas.png");
	private static final Drawable CRAFTING_GRID = new Drawable(BOOK_TEXTURE, 158, 181, 98, 58);
	private static final Drawable CRAFTING_COUNT = new Drawable(BOOK_TEXTURE, 104, 181, 34, 14);
	private static final Drawable RIGHT_BUTTON = new Drawable(BOOK_TEXTURE, 138, 181, 10, 9);
	private static final Drawable LEFT_BUTTON = new Drawable(BOOK_TEXTURE, 148, 181, 10, 9);
	//
	private final IRecipe[] recipes;
	private final ElementGroup recipeElement;
	//
	@Nullable
	private ElementGroup text;
	@Nullable
	private ButtonElement leftButton;
	@Nullable
	private ButtonElement rightButton;
	private int recipeIndex = 0;


	public CraftingElement(int xPos, int yPos, IRecipe[] recipes, boolean withTitle) {
		super(xPos, yPos, 98, 60 + (withTitle ? 12 : 2) + (recipes.length > 1 ? 14 : 0));
		this.recipes = recipes;

		setAlign(GuiElementAlignment.TOP_CENTER);

		int gridStartY = 2;
		//Title
		if (withTitle) {
			text(TextFormatting.DARK_GRAY + Translator.translateToLocal("for.gui.book.element.crafting"), GuiElementAlignment.TOP_CENTER);
			gridStartY = 12;
		}
		//Background
		drawable(0, gridStartY, CRAFTING_GRID);

		recipeElement = panel(0, gridStartY, width, height);

		//Recipe Switch
		if (recipes.length > 1) {
			//Recipe Count
			drawable(0, 0, CRAFTING_COUNT).setAlign(GuiElementAlignment.BOTTOM_CENTER);
			text = panel(width, height);
			leftButton = add(new ButtonElement(-27, -2, LEFT_BUTTON, e -> updateRecipe(recipeIndex - 1)));
			leftButton.setAlign(GuiElementAlignment.BOTTOM_CENTER);

			rightButton = add(new ButtonElement(27, -2, RIGHT_BUTTON, e -> updateRecipe(recipeIndex + 1)));
			rightButton.setAlign(GuiElementAlignment.BOTTOM_CENTER);
		}

		updateRecipe(0);
	}

	private void updateRecipe(int recipeIndex) {
		this.recipeIndex = recipeIndex;
		IRecipe recipe = recipes[recipeIndex];
		recipeElement.clear();
		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		//Output
		recipeElement.item(81, 21, recipe.getRecipeOutput());
		//Grid
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				int index = y * 3 + x;
				if (ingredients.size() <= index) {
					continue;
				}
				Ingredient ingredient = ingredients.get(index);
				recipeElement.add(new IngredientElement(1 + x * 20, 1 + y * 20, ingredient));
			}
		}
		if (text != null) {
			text.clear();
			text.text(TextFormatting.BLACK.toString() + (recipeIndex + 1) + "/" + recipes.length, GuiElementAlignment.BOTTOM_CENTER, 0).setYPosition(2);
		}
		if (leftButton != null) {
			leftButton.setEnabled(recipeIndex > 0);
		}
		if (rightButton != null) {
			rightButton.setEnabled(recipeIndex < recipes.length - 1);
		}
	}

}
