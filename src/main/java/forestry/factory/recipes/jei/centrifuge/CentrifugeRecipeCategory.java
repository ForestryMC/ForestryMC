package forestry.factory.recipes.jei.centrifuge;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.ingredients.GuiItemStackGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CentrifugeRecipeCategory extends ForestryRecipeCategory {

	public static final int[][] OUTPUTS = new int[][]{{0, 0}, {1, 0}, {2, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {1, 2}, {2, 2}};
	
	public static final Comparator<Entry<ItemStack, Float>> highestChanceComparator = new Comparator<Entry<ItemStack, Float>>() {
		@Override
		public int compare(Entry<ItemStack, Float> o1, Entry<ItemStack, Float> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	};
	
	private static final int inputSlot = 0;
	private static final int outputSlot = 1;
	
	private final static ResourceLocation guiTexture = new ResourceLocation("forestry", "textures/gui/squeezersocket.png");
	@Nonnull
	protected final IDrawableAnimated arrow;
	
	public CentrifugeRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 5, 14, 166, 65), "tile.for.factory.2.name");
		
		IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 0, 4, 17);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
	}
	
	@Nonnull
	@Override
	public String getUid() {
		return ForestryRecipeCategoryUid.CENTRIFUGE;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {	
	}

	@Override
	public void drawAnimations(Minecraft minecraft) {
		arrow.draw(minecraft, 53, 25);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		super.setRecipe(recipeLayout, recipeWrapper);
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		
		guiItemStacks.init(inputSlot, true, 25, 26);
		guiItemStacks.set(inputSlot, recipeWrapper.getInputs());
		CentrifugeRecipeWrapper centrifugeWrapper = (CentrifugeRecipeWrapper) recipeWrapper;
		setResults(centrifugeWrapper.getRecipe().getAllProducts(), (GuiItemStackGroup) guiItemStacks);
		guiItemStacks.addTooltipCallback(tooltip);
	}
	
	private void setResults(Map<ItemStack, Float> outputs, GuiItemStackGroup guiItemStacks) {
		Set<Entry<ItemStack, Float>> entrySet = outputs.entrySet();
		if (entrySet.size() == 0) {
			return;
		}
		PriorityQueue<Entry<ItemStack, Float>> sortByChance = new PriorityQueue<>(entrySet.size(), highestChanceComparator);
		sortByChance.addAll(entrySet);

		int i = 0;
		float[] chances = new float[sortByChance.size()];
		while (!sortByChance.isEmpty()) {
			Entry<ItemStack, Float> stack = sortByChance.poll();
			if (i >= OUTPUTS.length) {
				return;
			}
			int x = 3 + OUTPUTS[i][0] * 18;
			int y = 8 + OUTPUTS[i][1] * 18;
			int ID = outputSlot + i;
			guiItemStacks.init(ID, false, x, y);
			guiItemStacks.set(ID, stack.getKey());
			tooltip.addChanceTooltip(ID, stack.getValue());
			i++;
		}
	}

}
