package forestry.book.gui.elements;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import forestry.api.gui.GuiElementAlignment;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.IngredientElement;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.core.utils.Translator;

public class CraftingElement extends SelectionElement {
	private static final ResourceLocation BOOK_TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/atlas.png");
	private static final Drawable CRAFTING_GRID = new Drawable(BOOK_TEXTURE, 158, 181, 98, 58);
	//
	private final IRecipe[] recipes;
	private final ElementGroup recipeElement;


	public CraftingElement(int xPos, int yPos, IRecipe[] recipes, boolean withTitle) {
		super(xPos, yPos, 98, 60 + (withTitle ? 12 : 2) + (recipes.length > 1 ? 14 : 0), recipes.length > 1);
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

		updateIndex(0);
	}

	protected void updateIndex(int index) {
		this.index = index;
		IRecipe recipe = recipes[index];
		recipeElement.clear();
		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		//Output
		recipeElement.item(81, 21, recipe.getRecipeOutput());
		//Grid
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				int i = y * 3 + x;
				if (ingredients.size() <= i) {
					continue;
				}
				Ingredient ingredient = ingredients.get(i);
				recipeElement.add(new IngredientElement(1 + x * 20, 1 + y * 20, ingredient));
			}
		}
		if (text != null) {
			text.clear();
			text.text(TextFormatting.BLACK.toString() + (index + 1) + "/" + recipes.length, GuiElementAlignment.BOTTOM_CENTER, 0).setYPosition(2);
		}
		if (leftButton != null) {
			leftButton.setEnabled(index > 0);
		}
		if (rightButton != null) {
			rightButton.setEnabled(index < recipes.length - 1);
		}
	}

}
