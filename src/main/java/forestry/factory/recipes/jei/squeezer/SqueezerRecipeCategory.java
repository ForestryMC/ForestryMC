package forestry.factory.recipes.jei.squeezer;

import com.mojang.blaze3d.vertex.PoseStack;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.core.config.Constants;
import forestry.core.recipes.jei.ChanceTooltipCallback;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeType;
import forestry.core.utils.JeiUtil;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.features.FactoryBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SqueezerRecipeCategory extends ForestryRecipeCategory<ISqueezerRecipe> {
	private static final ResourceLocation guiTexture = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/squeezersocket.png");

	private final IDrawableAnimated arrow;
	private final IDrawable tankOverlay;
	private final IDrawable icon;
	private final ICraftingGridHelper craftingGridHelper;

	public SqueezerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 9, 16, 158, 62), "block.forestry.squeezer");

		IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 60, 43, 18);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
		ItemStack squeezer = new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.SQUEEZER).block());
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, squeezer);
		this.craftingGridHelper = guiHelper.createCraftingGridHelper();
	}

	@Override
	public RecipeType<ISqueezerRecipe> getRecipeType() {
		return ForestryRecipeType.SQUEEZER;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ISqueezerRecipe recipe, IFocusGroup focuses) {
		List<IRecipeSlotBuilder> craftingSlots = JeiUtil.layoutSlotGrid(builder, RecipeIngredientRole.INPUT, 3, 3, 8, 5, 18);
		JeiUtil.setCraftingItems(craftingSlots, recipe.getResources(), 3, 3, craftingGridHelper);

		builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 44)
				.addTooltipCallback(new ChanceTooltipCallback(recipe.getRemnantsChance()))
				.addItemStack(recipe.getRemnants());

		builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 2)
				.setFluidRenderer(10000, false, 16, 58)
				.setOverlay(tankOverlay, 0, 0)
				.addIngredient(ForgeTypes.FLUID_STACK, recipe.getFluidOutput());
	}

	@Override
	public void draw(ISqueezerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
		arrow.draw(stack, 67, 25);
	}
}
