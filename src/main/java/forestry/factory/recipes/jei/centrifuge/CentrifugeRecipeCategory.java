package forestry.factory.recipes.jei.centrifuge;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.core.recipes.jei.ForestryTooltipCallback;
import forestry.core.render.ForestryResource;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.ingredients.GuiItemStackGroup;

public class CentrifugeRecipeCategory extends ForestryRecipeCategory {

	private static final int[][] OUTPUTS = new int[][]{{0, 0}, {1, 0}, {2, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {1, 2}, {2, 2}};
	
	private static final Comparator<Entry<ItemStack, Float>> highestChanceComparator = new Comparator<Entry<ItemStack, Float>>() {
		@Override
		public int compare(Entry<ItemStack, Float> o1, Entry<ItemStack, Float> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	};
	
	private static final int inputSlot = 0;
	private static final int outputSlot = 1;
	
	private final static ResourceLocation guiTexture = new ForestryResource("textures/gui/centrifugesocket.png");
	@Nonnull
	private final IDrawableAnimated arrow;
	private final ForestryTooltipCallback tooltip = new ForestryTooltipCallback();
	
	public CentrifugeRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 5, 14, 166, 65), "tile.for.factory.centrifuge.name");
		
		IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 0, 4, 17);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 80, IDrawableAnimated.StartDirection.BOTTOM, false);
	}
	
	@Nonnull
	@Override
	public String getUid() {
		return ForestryRecipeCategoryUid.CENTRIFUGE;
	}

	@Override
	public void drawAnimations(@Nonnull Minecraft minecraft) {
		arrow.draw(minecraft, 53, 22);
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		
		guiItemStacks.init(inputSlot, true, 24, 22);
		guiItemStacks.setFromRecipe(inputSlot, recipeWrapper.getInputs());
		CentrifugeRecipeWrapper centrifugeWrapper = (CentrifugeRecipeWrapper) recipeWrapper;
		setResults(centrifugeWrapper.getRecipe().getAllProducts(), (GuiItemStackGroup) guiItemStacks);
		guiItemStacks.addTooltipCallback(tooltip);
	}
	
	private void setResults(Map<ItemStack, Float> outputs, GuiItemStackGroup guiItemStacks) {
		Set<Entry<ItemStack, Float>> entrySet = outputs.entrySet();
		if (entrySet.isEmpty()) {
			return;
		}
		PriorityQueue<Entry<ItemStack, Float>> sortByChance = new PriorityQueue<>(entrySet.size(), highestChanceComparator);
		sortByChance.addAll(entrySet);

		int i = 0;
		while (!sortByChance.isEmpty()) {
			Entry<ItemStack, Float> stack = sortByChance.poll();
			if (i >= OUTPUTS.length) {
				return;
			}
			int x = 92 + OUTPUTS[i][0] * 18;
			int y = 4 + OUTPUTS[i][1] * 18;
			int ID = outputSlot + i;
			guiItemStacks.init(ID, false, x, y);
			guiItemStacks.set(ID, stack.getKey());
			tooltip.addChanceTooltip(ID + 1, stack.getValue());
			i++;
		}
	}

}
