package forestry.book.gui.elements;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.gui.GuiElementAlignment;
import forestry.book.gui.GuiForesterBook;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.IngredientElement;

@OnlyIn(Dist.CLIENT)
public class CraftingElement extends SelectionElement<IRecipe> {
	private static final Drawable CRAFTING_GRID = new Drawable(GuiForesterBook.TEXTURE, 158, 181, 98, 58);

	public CraftingElement(int xPos, int yPos, IRecipe[] recipes) {
		super(xPos, yPos, 98, 62, recipes, 2);

		setAlign(GuiElementAlignment.TOP_CENTER);
		//Background
		drawable(0, 2, CRAFTING_GRID);
		add(selectedElement);
		setIndex(0);
	}

	protected void onIndexUpdate(int index, IRecipe recipe) {
		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		//Output
		selectedElement.item(81, 21, recipe.getRecipeOutput());
		//Grid
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				int i = y * 3 + x;
				if (ingredients.size() <= i) {
					continue;
				}
				Ingredient ingredient = ingredients.get(i);
				selectedElement.add(new IngredientElement(1 + x * 20, 1 + y * 20, ingredient));
			}
		}
	}

}
