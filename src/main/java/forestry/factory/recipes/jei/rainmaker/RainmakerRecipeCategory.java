package forestry.factory.recipes.jei.rainmaker;

import javax.annotation.Nonnull;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeWrapper;

public class RainmakerRecipeCategory extends ForestryRecipeCategory {
	private static final int SLOT_INPUT_INDEX = 0;
	private final IDrawable slot;

	public RainmakerRecipeCategory(@Nonnull IGuiHelper guiHelper) {
		super(guiHelper.createBlankDrawable(150, 30), "tile.for.rainmaker.name");
		this.slot = guiHelper.getSlotDrawable();
	}

	@Nonnull
	@Override
	public String getUid() {
		return ForestryRecipeCategoryUid.RAINMAKER;
	}

	@Override
	public void drawExtras(@Nonnull Minecraft minecraft) {
		super.drawExtras(minecraft);
		slot.draw(minecraft);
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.init(SLOT_INPUT_INDEX, true, 0, 0);
		if (recipeWrapper instanceof RainmakerRecipeWrapper) {
			RainmakerRecipeWrapper rainmakerRecipeWrapper = (RainmakerRecipeWrapper) recipeWrapper;
			List<ItemStack> input = rainmakerRecipeWrapper.getInputs().get(SLOT_INPUT_INDEX);
			guiItemStacks.set(SLOT_INPUT_INDEX, input);
		}
	}
}
