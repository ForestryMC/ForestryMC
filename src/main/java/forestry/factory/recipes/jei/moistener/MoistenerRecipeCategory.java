package forestry.factory.recipes.jei.moistener;

import com.mojang.blaze3d.vertex.PoseStack;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.IMoistenerRecipe;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class MoistenerRecipeCategory extends ForestryRecipeCategory<MoistenerRecipe> {
	private static final ResourceLocation guiTexture = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/moistener.png");

	private final IDrawableAnimated arrow;
	private final IDrawableAnimated progressBar;
	private final IDrawable tankOverlay;
	private final IDrawable icon;

	public MoistenerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 15, 15, 145, 60), "block.forestry.moistener");

		IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 91, 29, 55);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 80, IDrawableAnimated.StartDirection.BOTTOM, false);
		IDrawableStatic progressBar = guiHelper.createDrawable(guiTexture, 176, 74, 16, 15);
		this.progressBar = guiHelper.createAnimatedDrawable(progressBar, 160, IDrawableAnimated.StartDirection.LEFT, false);
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
		ItemStack moistener = new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.MOISTENER).block());
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, moistener);
	}

	@Override
	public ResourceLocation getUid() {
		return ForestryRecipeCategoryUid.MOISTENER;
	}

	@Override
	public Class<? extends MoistenerRecipe> getRecipeClass() {
		return MoistenerRecipe.class;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, MoistenerRecipe recipeWrapper, List<? extends IFocus<?>> focuses) {
		IMoistenerRecipe recipe = recipeWrapper.recipe();
		MoistenerFuel fuel = recipeWrapper.fuel();

		builder.addSlot(RecipeIngredientRole.INPUT, 128, 4)
				.addIngredients(recipe.getResource());

		builder.addSlot(RecipeIngredientRole.INPUT, 24, 43)
				.addIngredients(fuel.getResource());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 128, 40)
				.addItemStack(recipe.getProduct());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 22)
				.addItemStack(fuel.getProduct());

		FluidStack fluidInput = new FluidStack(Fluids.WATER, recipe.getTimePerItem() / 4);
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
				.setFluidRenderer(10000, false, 16, 58)
				.setOverlay(tankOverlay, 0, 0)
				.addIngredient(VanillaTypes.FLUID, fluidInput);
	}

	@Override
	public void draw(MoistenerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
		arrow.draw(stack, 78, 2);
		progressBar.draw(stack, 109, 22);
	}
}
