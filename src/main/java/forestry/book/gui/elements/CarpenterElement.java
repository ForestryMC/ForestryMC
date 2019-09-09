package forestry.book.gui.elements;

import java.util.Collection;
import java.util.stream.Stream;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.IngredientElement;
import forestry.core.gui.elements.TankElement;
import forestry.factory.recipes.CarpenterRecipeManager;

@OnlyIn(Dist.CLIENT)
public class CarpenterElement extends SelectionElement<ICarpenterRecipe> {
	private static final ResourceLocation BOOK_CRAFTING_TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/almanac/crafting.png");
	private static final Drawable CARPENTER_BACKGROUND = new Drawable(BOOK_CRAFTING_TEXTURE, 0, 0, 108, 60);
	private static final Drawable CARPENTER_TANK_OVERLAY = new Drawable(BOOK_CRAFTING_TEXTURE, 109, 1, 16, 58);

	public CarpenterElement(int xPos, int yPos, ItemStack stack) {
		this(0, 0, new ItemStack[]{stack});
	}

	public CarpenterElement(int xPos, int yPos, ItemStack[] stacks) {
		this(0, 0, Stream.of(stacks).map(CarpenterRecipeManager::getRecipes).flatMap(Collection::stream).toArray(ICarpenterRecipe[]::new));
	}

	public CarpenterElement(int xPos, int yPos, ICarpenterRecipe[] recipes) {
		super(xPos, yPos, 108, 62, recipes, 2);

		drawable(0, 2, CARPENTER_BACKGROUND);
		add(selectedElement);
		setIndex(0);
	}

	@Override
	protected void onIndexUpdate(int index, ICarpenterRecipe recipe) {
		selectedElement.add(new TankElement(91, 1, null, recipe.getFluidResource(), Constants.PROCESSOR_TANK_CAPACITY, CARPENTER_TANK_OVERLAY));
		IDescriptiveRecipe gridRecipe = recipe.getCraftingGridRecipe();
		NonNullList<NonNullList<ItemStack>> ingredients = gridRecipe.getRawIngredients();
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				int ingredientIndex = y * 3 + x;
				if (ingredientIndex >= ingredients.size()) {
					continue;
				}
				NonNullList<ItemStack> items = ingredients.get(ingredientIndex);
				selectedElement.add(new IngredientElement(1 + x * 19, 3 + y * 19, Ingredient.fromStacks(items.toArray(new ItemStack[0]))));
			}
		}
		selectedElement.item(71, 41, gridRecipe.getOutput());
	}
}
