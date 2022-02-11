package forestry.factory.recipes.jei.still;

import com.mojang.blaze3d.vertex.PoseStack;
import forestry.api.recipes.IStillRecipe;
import forestry.core.config.Constants;
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

import java.util.List;

public class StillRecipeCategory extends ForestryRecipeCategory<IStillRecipe> {
	private static final ResourceLocation guiTexture = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/still.png");

	private final IDrawable tankOverlay;
	private final IDrawable icon;
	private final IDrawableAnimated progressBar;

	public StillRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 34, 14, 108, 60), "block.forestry.still");
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);

		IDrawableStatic progressBarDrawable0 = guiHelper.createDrawable(guiTexture, 176, 74, 4, 18);
		this.progressBar = guiHelper.createAnimatedDrawable(progressBarDrawable0, 20, IDrawableAnimated.StartDirection.BOTTOM, false);
		ItemStack still = new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.STILL).block());
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, still);
	}

	@Override
	public ResourceLocation getUid() {
		return ForestryRecipeCategoryUid.STILL;
	}

	@Override
	public Class<? extends IStillRecipe> getRecipeClass() {
		return IStillRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, IStillRecipe recipe, List<? extends IFocus<?>> focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
				.setFluidRenderer(10000, false, 16, 58)
				.setOverlay(tankOverlay, 0, 0)
				.addIngredient(VanillaTypes.FLUID, recipe.getInput());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 1)
				.setFluidRenderer(10000, false, 16, 58)
				.setOverlay(tankOverlay, 0, 0)
				.addIngredient(VanillaTypes.FLUID, recipe.getOutput());
	}

	@Override
	public void draw(IStillRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
		progressBar.draw(stack, 50, 3);
	}
}
