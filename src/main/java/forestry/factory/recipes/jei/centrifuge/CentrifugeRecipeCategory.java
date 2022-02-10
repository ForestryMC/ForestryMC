package forestry.factory.recipes.jei.centrifuge;

import com.mojang.blaze3d.vertex.PoseStack;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.core.config.Constants;
import forestry.core.recipes.jei.ChanceTooltipCallback;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.features.FactoryBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.List;

public class CentrifugeRecipeCategory extends ForestryRecipeCategory<ICentrifugeRecipe> {
	private static final ResourceLocation guiTexture = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/centrifugesocket2.png");

	private final IDrawableAnimated arrow;
	private final IDrawable icon;

	public CentrifugeRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 11, 18, 154, 54), "block.forestry.centrifuge");

		IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 0, 4, 17);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 80, IDrawableAnimated.StartDirection.BOTTOM, false);
		ItemStack centrifuge = new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.CENTRIFUGE).block());
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, centrifuge);
	}

	@Override
	public ResourceLocation getUid() {
		return ForestryRecipeCategoryUid.CENTRIFUGE;
	}

	@Override
	public Class<? extends ICentrifugeRecipe> getRecipeClass() {
		return ICentrifugeRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ICentrifugeRecipe recipe, List<? extends IFocus<?>> focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 5, 19)
			.addIngredients(recipe.getInput());

		List<ICentrifugeRecipe.Product> sortedProducts = recipe.getAllProducts().stream()
				.sorted(Comparator.comparing(ICentrifugeRecipe.Product::getProbability).reversed())
				.toList();

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				int index = x + (y * 3);
				if (index >= sortedProducts.size()) {
					break;
				}
				ICentrifugeRecipe.Product product = sortedProducts.get(index);
				int xPos = 101 + x * 18;
				int yPos = 1 + y * 18;
				builder.addSlot(RecipeIngredientRole.OUTPUT, xPos, yPos)
						.addItemStack(product.getStack())
						.addTooltipCallback(new ChanceTooltipCallback(product.getProbability()));
			}
		}
	}

	@Override
	public void draw(ICentrifugeRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		arrow.draw(matrixStack, 32, 18);
		arrow.draw(matrixStack, 56, 18);
	}
}
