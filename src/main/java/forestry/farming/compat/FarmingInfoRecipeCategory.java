//package forestry.farming.compat;
//
//import java.util.List;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.ResourceLocation;
//
//import forestry.core.config.Constants;
//import forestry.core.recipes.jei.ForestryRecipeCategory;
//
//import mezz.jei.api.IGuiHelper;
//import mezz.jei.api.gui.IDrawable;
//import mezz.jei.api.gui.IGuiItemStackGroup;
//import mezz.jei.api.gui.IRecipeLayout;
//import mezz.jei.api.ingredients.IIngredients;
//import mezz.jei.api.ingredients.VanillaTypes;
//
//public class FarmingInfoRecipeCategory extends ForestryRecipeCategory<FarmingInfoRecipeWrapper> {
//	public static final String UID = "forestry.farming";
//	private final IDrawable slotDrawable;
//	private final IDrawable addition;
//	private final IDrawable arrow;
//
//	public FarmingInfoRecipeCategory(IGuiHelper guiHelper) {
//		super(guiHelper.createBlankDrawable(144, 90), "for.jei.farming.name");
//		this.slotDrawable = guiHelper.getSlotDrawable();
//		ResourceLocation resourceLocation = new ResourceLocation(Constants.MOD_ID, "textures/gui/jei/recipes.png");
//		addition = guiHelper.createDrawable(resourceLocation, 44, 0, 15, 15);
//		arrow = guiHelper.createDrawable(resourceLocation, 59, 0, 15, 15);
//	}
//
//	@Override
//	public String getUid() {
//		return UID;
//	}
//
//	@Override
//	public void drawExtras(Minecraft minecraft) {
//		slotDrawable.draw(minecraft, 63, 18);
//		for (int x = 0; x < 2; x++) {
//			for (int y = 0; y < 2; y++) {
//				slotDrawable.draw(minecraft, x * 18, 54 + y * 18);
//			}
//		}
//
//		addition.draw(minecraft, 37, 64);
//
//		for (int x = 0; x < 2; x++) {
//			for (int y = 0; y < 2; y++) {
//				slotDrawable.draw(minecraft, 54 + x * 18, 54 + y * 18);
//			}
//		}
//
//		arrow.draw(minecraft, 91, 64);
//
//		for (int x = 0; x < 2; x++) {
//			for (int y = 0; y < 2; y++) {
//				slotDrawable.draw(minecraft, 108 + x * 18, 54 + y * 18);
//			}
//		}
//	}
//
//	@Override
//	public void setRecipe(IRecipeLayout recipeLayout, FarmingInfoRecipeWrapper recipeWrapper, IIngredients ingredients) {
//		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
//		List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
//		List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);
//		guiItemStacks.init(0, true, 63, 18);
//		guiItemStacks.set(0, inputs.getComb(0));
//		for (int x = 0; x < 2; x++) {
//			for (int y = 0; y < 2; y++) {
//				int index = 1 + x + y * 2;
//				guiItemStacks.init(index, true, x * 18, 54 + y * 18);
//				if (inputs.size() > index) {
//					List<ItemStack> stack = inputs.getComb(index);
//					guiItemStacks.set(index, stack);
//				}
//			}
//		}
//		for (int x = 0; x < 2; x++) {
//			for (int y = 0; y < 2; y++) {
//				int index = 5 + x + y * 2;
//				guiItemStacks.init(index, true, 54 + x * 18, 54 + y * 18);
//				if (inputs.size() > index) {
//					List<ItemStack> stack = inputs.getComb(index);
//					guiItemStacks.set(index, stack);
//				}
//			}
//		}
//		for (int x = 0; x < 2; x++) {
//			for (int y = 0; y < 2; y++) {
//				int index = 9 + x + y * 2;
//				guiItemStacks.init(index, false, 108 + x * 18, 54 + y * 18);
//				if (outputs.size() > x + y * 2) {
//					List<ItemStack> stack = outputs.getComb(x + y * 2);
//					guiItemStacks.set(index, stack);
//				}
//			}
//		}
//	}
//}
