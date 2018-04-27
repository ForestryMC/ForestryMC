package forestry.book.data.content;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.book.BookContent;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiElementFactory;
import forestry.book.data.CraftingData;
import forestry.book.gui.elements.CraftingElement;

/**
 * A book content that displays one ore more crafting recipes.
 */
@SideOnly(Side.CLIENT)
public class CraftingContent extends BookContent<CraftingData> {
	@Override
	public Class<CraftingData> getDataClass() {
		return CraftingData.class;
	}

	@Override
	public boolean addElements(IElementGroup page, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement, int pageHeight) {
		if (data == null || data.locations.length == 0) {
			return false;
		}
		List<IRecipe> recipes = new LinkedList<>();
		for (ResourceLocation location : data.locations) {
			IRecipe recipe = ForgeRegistries.RECIPES.getValue(location);
			if (recipe != null) {
				recipes.add(recipe);
			}
		}
		page.add(new CraftingElement(0, 0, recipes.toArray(new IRecipe[0])));
		return true;
	}
}
