package forestry.book.data.content;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fluids.FluidTankInfo;

import forestry.api.book.BookContent;
import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiElementFactory;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.book.data.CraftingData;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.IngredientElement;
import forestry.core.gui.elements.TankElement;
import forestry.core.gui.elements.layouts.ElementGroup;
import forestry.factory.recipes.CarpenterRecipeManager;

public class CarpenterContent extends BookContent<CraftingData> {
	private static final ResourceLocation BOOK_CRAFTING_TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/atlas_crafting.png");
	private static final Drawable CARPENTER_BACKGROUND = new Drawable(BOOK_CRAFTING_TEXTURE, 0, 0, 108, 60);
	private static final Drawable CARPENTER_TANK_OVERLAY = new Drawable(BOOK_CRAFTING_TEXTURE, 109, 1, 16, 58);

	@Nullable
	@Override
	public Class<? extends CraftingData> getDataClass() {
		return CraftingData.class;
	}

	@Nullable
	@Override
	public boolean addElements(IElementGroup group, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement) {
		if(data == null || data.stack.isEmpty()){
			return false;
		}
		boolean firstRecipe = /*previous instanceof CarpenterContent*/false;
		for(ICarpenterRecipe recipe : CarpenterRecipeManager.getRecipes(data.stack)) {
			if (recipe == null) {
				continue;
			}
			ElementGroup panel = GuiElementFactory.INSTANCE.createPanel(0, 0, 108, firstRecipe ? 72 : 60);
			int gridStartY = 0;
			if (firstRecipe) {
				panel.text(TextFormatting.DARK_GRAY + "Carpenter", GuiElementAlignment.TOP_CENTER);
				gridStartY = 12;
			}
			panel.drawable(0, gridStartY, CARPENTER_BACKGROUND);
			panel.add(new TankElement(91, gridStartY + 1, null, () -> new FluidTankInfo(recipe.getFluidResource(), Constants.PROCESSOR_TANK_CAPACITY), CARPENTER_TANK_OVERLAY));
			IDescriptiveRecipe gridRecipe = recipe.getCraftingGridRecipe();
			NonNullList<NonNullList<ItemStack>> ingredients = gridRecipe.getRawIngredients();
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					int index = y * 3 + x;
					if (index >= ingredients.size()) {
						continue;
					}
					NonNullList<ItemStack> items = ingredients.get(index);
					panel.add(new IngredientElement(1 + x * 19, gridStartY + 3 + y * 19, Ingredient.fromStacks(items.toArray(new ItemStack[items.size()]))));
				}
			}
			panel.item(71, gridStartY + 41, gridRecipe.getOutput());
			group.add(panel);
		}
		return true;
	}
}
