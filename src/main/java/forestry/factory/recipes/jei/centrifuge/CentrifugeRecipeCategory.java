//package forestry.factory.recipes.jei.centrifuge;
//
//import java.util.Comparator;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.PriorityQueue;
//import java.util.Queue;
//import java.util.Set;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.ResourceLocation;
//
//import forestry.core.recipes.jei.ForestryRecipeCategory;
//import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
//import forestry.core.recipes.jei.ForestryTooltipCallback;
//import forestry.core.render.ForestryResource;
//
//import mezz.jei.api.IGuiHelper;
//import mezz.jei.api.gui.IDrawableAnimated;
//import mezz.jei.api.gui.IDrawableStatic;
//import mezz.jei.api.gui.IGuiItemStackGroup;
//import mezz.jei.api.gui.IRecipeLayout;
//import mezz.jei.api.ingredients.IIngredients;
//import mezz.jei.api.ingredients.VanillaTypes;
//
//public class CentrifugeRecipeCategory extends ForestryRecipeCategory<CentrifugeRecipeWrapper> {
//
//	private static final int[][] OUTPUTS = new int[][]{{0, 0}, {1, 0}, {2, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {1, 2}, {2, 2}};
//
//	private static final Comparator<Entry<ItemStack, Float>> highestChanceComparator = (o1, o2) -> o2.getValue().compareTo(o1.getValue());
//
//	private static final int inputSlot = 0;
//	private static final int outputSlot = 1;
//
//	private final static ResourceLocation guiTexture = new ForestryResource("textures/gui/centrifugesocket2.png");
//	private final IDrawableAnimated arrow;
//
//	public CentrifugeRecipeCategory(IGuiHelper guiHelper) {
//		super(guiHelper.createDrawable(guiTexture, 11, 18, 154, 54), "block.forestry.centrifuge.name");
//
//		IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 0, 4, 17);
//		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 80, IDrawableAnimated.StartDirection.BOTTOM, false);
//	}
//
//
//	@Override
//	public String getUid() {
//		return ForestryRecipeCategoryUid.CENTRIFUGE;
//	}
//
//	@Override
//	public void drawExtras(Minecraft minecraft) {
//		arrow.draw(minecraft, 32, 18);
//		arrow.draw(minecraft, 56, 18);
//	}
//
//	@Override
//	public void setRecipe(IRecipeLayout recipeLayout, CentrifugeRecipeWrapper recipeWrapper, IIngredients ingredients) {
//		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
//
//		guiItemStacks.init(inputSlot, true, 4, 18);
//		List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
//		guiItemStacks.set(inputSlot, inputs.getComb(0));
//
//		ForestryTooltipCallback tooltip = new ForestryTooltipCallback();
//		Map<ItemStack, Float> products = recipeWrapper.getRecipe().getAllProducts();
//		setResults(tooltip, products, guiItemStacks);
//		guiItemStacks.addTooltipCallback(tooltip);
//	}
//
//	private static void setResults(ForestryTooltipCallback tooltip, Map<ItemStack, Float> outputs, IGuiItemStackGroup guiItemStacks) {
//		Set<Entry<ItemStack, Float>> entrySet = outputs.entrySet();
//		if (entrySet.isEmpty()) {
//			return;
//		}
//		Queue<Entry<ItemStack, Float>> sortByChance = new PriorityQueue<>(entrySet.size(), highestChanceComparator);
//		sortByChance.addAll(entrySet);
//
//		int i = 0;
//		while (!sortByChance.isEmpty()) {
//			Entry<ItemStack, Float> stack = sortByChance.poll();
//			if (i >= OUTPUTS.length) {
//				return;
//			}
//			int x = 100 + OUTPUTS[i][0] * 18;
//			int y = OUTPUTS[i][1] * 18;
//			int slotIndex = outputSlot + i;
//			guiItemStacks.init(slotIndex, false, x, y);
//			guiItemStacks.set(slotIndex, stack.getKey());
//			tooltip.addChanceTooltip(slotIndex, stack.getValue());
//			i++;
//		}
//	}
//
//}
