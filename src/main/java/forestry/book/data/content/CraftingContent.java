package forestry.book.data.content;

import javax.annotation.Nullable;

import net.minecraft.item.crafting.IRecipe;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import forestry.api.book.BookContent;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiElementFactory;
import forestry.book.data.CraftingData;
import forestry.book.gui.elements.CraftingElement;

public class CraftingContent extends BookContent<CraftingData> {
	@Override
	public Class<CraftingData> getDataClass() {
		return CraftingData.class;
	}

	@Nullable
	@Override
	public boolean addElements(IElementGroup page, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement) {
		if (data == null || data.locations.length == 0) {
			return false;
		}
		IRecipe[] recipes = new IRecipe[data.locations.length];
		for (int i = 0; i < recipes.length; i++) {
			recipes[i] = ForgeRegistries.RECIPES.getValue(data.locations[i]);
		}
		page.add(new CraftingElement(0, 0, recipes, false));
		return true;
	}
}
