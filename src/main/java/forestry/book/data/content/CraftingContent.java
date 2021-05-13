package forestry.book.data.content;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.book.BookContent;
import forestry.book.data.CraftingData;
import forestry.book.gui.elements.CraftingElement;
import forestry.core.gui.elements.GuiElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.layouts.ContainerElement;
import forestry.core.utils.RecipeUtils;

/**
 * A book content that displays one ore more crafting recipes.
 */
@OnlyIn(Dist.CLIENT)
public class CraftingContent extends BookContent<CraftingData> {
	@Override
	public Class<CraftingData> getDataClass() {
		return CraftingData.class;
	}

	@Override
	public boolean addElements(ContainerElement page, GuiElementFactory factory, @Nullable BookContent<?> previous, @Nullable GuiElement previousElement, int pageHeight) {
		if (data == null || data.locations.length == 0) {
			return false;
		}
		RecipeManager manager = RecipeUtils.getRecipeManager(null);
		if (manager == null) {
			//Failed to load recipe manager
			return false;
		}
		List<IRecipe<?>> recipes = new LinkedList<>();
		for (ResourceLocation location : data.locations) {
			Map<ResourceLocation, IRecipe<CraftingInventory>> recipeMap = manager.byType(IRecipeType.CRAFTING);
			IRecipe<?> recipe = recipeMap.get(location);
			if (recipe != null) {
				recipes.add(recipe);
			}
		}
		page.add(new CraftingElement(recipes.toArray(new IRecipe[0])));
		return true;
	}
}
