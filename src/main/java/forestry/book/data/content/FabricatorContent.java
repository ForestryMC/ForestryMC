package forestry.book.data.content;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.Fluid;

import forestry.api.book.BookContent;
import forestry.api.gui.IElementGroup;
import forestry.api.gui.IGuiElement;
import forestry.api.gui.IGuiElementFactory;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.book.data.CraftingData;
import forestry.book.gui.elements.FabricatorElement;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.factory.recipes.FabricatorSmeltingRecipeManager;

public class FabricatorContent extends BookContent<CraftingData> {
	private static final ResourceLocation BOOK_CRAFTING_TEXTURE = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/almanac/crafting.png");
	private static final Drawable FABRICATOR_BACKGROUND = new Drawable(BOOK_CRAFTING_TEXTURE, 0, 60, 108, 60);
	private static final Drawable FABRICATOR_TANK_OVERLAY = new Drawable(BOOK_CRAFTING_TEXTURE, 109, 61, 16, 16);
	@Override
	public Class<? extends CraftingData> getDataClass() {
		return CraftingData.class;
	}

	@Override
	public boolean addElements(IElementGroup page, IGuiElementFactory factory, @Nullable BookContent previous, @Nullable IGuiElement previousElement, int pageHeight) {
		if(data == null || (data.stack.isEmpty() && data.stacks.length == 0)){
			return false;
		}
		if(!data.stack.isEmpty()) {
			page.add(new FabricatorElement(0, 0, data.stack));
		}else{
			page.add(new FabricatorElement(0, 0, data.stacks));
		}
		return true;
	}

	private static Map<Fluid, List<IFabricatorSmeltingRecipe>> getSmeltingInputs() {
		Map<Fluid, List<IFabricatorSmeltingRecipe>> smeltingInputs = new HashMap<>();
		for (IFabricatorSmeltingRecipe smelting : FabricatorSmeltingRecipeManager.recipes) {
			Fluid fluid = smelting.getProduct().getFluid();
			if (!smeltingInputs.containsKey(fluid)) {
				smeltingInputs.put(fluid, new ArrayList<>());
			}
			smeltingInputs.get(fluid).add(smelting);
		}
		return smeltingInputs;
	}
}
