package forestry.factory.recipes.jei.fermenter;

import com.mojang.blaze3d.vertex.PoseStack;
import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IVariableFermentable;
import forestry.core.config.Constants;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeType;
import forestry.factory.blocks.BlockTypeFactoryTesr;
import forestry.factory.features.FactoryBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FermenterRecipeCategory extends ForestryRecipeCategory<IFermenterRecipe> {
	private static final ResourceLocation guiTexture = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/fermenter.png");

	private final IDrawableAnimated progressBar0;
	private final IDrawableAnimated progressBar1;
	private final IDrawable tankOverlay;
	private final IDrawable icon;

	public FermenterRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 34, 18, 108, 60), "block.forestry.fermenter");

		IDrawableStatic progressBarDrawable0 = guiHelper.createDrawable(guiTexture, 176, 60, 4, 18);
		this.progressBar0 = guiHelper.createAnimatedDrawable(progressBarDrawable0, 40, IDrawableAnimated.StartDirection.BOTTOM, false);
		IDrawableStatic progressBarDrawable1 = guiHelper.createDrawable(guiTexture, 176, 78, 4, 18);
		this.progressBar1 = guiHelper.createAnimatedDrawable(progressBarDrawable1, 80, IDrawableAnimated.StartDirection.BOTTOM, false);
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 192, 0, 16, 58);
		ItemStack fermenter = new ItemStack(FactoryBlocks.TESR.get(BlockTypeFactoryTesr.FERMENTER).block());
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, fermenter);
	}

	@Override
	public RecipeType<IFermenterRecipe> getRecipeType() {
		return ForestryRecipeType.FERMENTER;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, IFermenterRecipe recipe, IFocusGroup focuses) {
		IRecipeSlotBuilder ingredientInputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 51, 5)
			.addIngredients(recipe.getResource());

		Collection<FermenterFuel> fuels = FuelManager.fermenterFuel.values();
		List<ItemStack> fuelInputs = fuels.stream().map(FermenterFuel::getItem).toList();
		builder.addSlot(RecipeIngredientRole.INPUT, 41, 39)
				.addItemStacks(fuelInputs);

		FluidStack fluidInput = recipe.getFluidResource().copy();
		fluidInput.setAmount(recipe.getFermentationValue());
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
				.setFluidRenderer(3000, false, 16, 58)
				.setOverlay(tankOverlay, 0, 0)
				.addIngredient(ForgeTypes.FLUID_STACK, fluidInput);

		final int baseAmount = Math.round(recipe.getFermentationValue() * recipe.getModifier());
		List<FluidStack> outputs =
			Arrays.stream(recipe.getResource().getItems())
				.map(fermentable -> {
					int amount = baseAmount;
					if (fermentable.getItem() instanceof IVariableFermentable variableFermentable) {
						amount *= variableFermentable.getFermentationModifier(fermentable);
					}
					return new FluidStack(recipe.getOutput(), amount);
				})
				.toList();

		IRecipeSlotBuilder fluidOutputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 1)
				.setFluidRenderer(3000, false, 16, 58)
				.setOverlay(tankOverlay, 0, 0)
				.addIngredients(ForgeTypes.FLUID_STACK, outputs);

		builder.createFocusLink(ingredientInputSlot, fluidOutputSlot);
	}

	@Override
	public void draw(IFermenterRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
		progressBar0.draw(stack, 40, 14);
		progressBar1.draw(stack, 64, 28);
	}
}
