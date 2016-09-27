package forestry.factory.recipes.jei.carpenter;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.core.render.ForestryResource;
import forestry.factory.recipes.jei.FactoryJeiPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class CarpenterRecipeCategory extends ForestryRecipeCategory<CarpenterRecipeWrapper> {

	private static final int boxSlot = 0;
	private static final int craftOutputSlot = 1;
	private static final int craftInputSlot = 2;
	
	private static final int inputTank = 0;
	
	private final static ResourceLocation guiTexture = new ForestryResource("textures/gui/carpenter.png");
	@Nonnull
	private final ICraftingGridHelper craftingGridHelper;
	@Nonnull
	private final IDrawableAnimated arrow;
	@Nonnull
	private final IDrawable tankOverlay;
	
	public CarpenterRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 9, 16, 158, 61), "tile.for.carpenter.name");
		
		craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot, craftOutputSlot);
		IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 59, 4, 17);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
	}
	
	@Nonnull
	@Override
	public String getUid() {
		return ForestryRecipeCategoryUid.CARPENTER;
	}

	@Override
	public void drawAnimations(@Nonnull Minecraft minecraft) {
		arrow.draw(minecraft, 89, 34);
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull CarpenterRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		
		guiItemStacks.init(boxSlot, true, 73, 3);
		
		guiItemStacks.init(craftOutputSlot, false, 70, 34);
		
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 3; ++x) {
				int index = craftInputSlot + x + y * 3;
				guiItemStacks.init(index, true, x * 18, 3 + y * 18);
			}
		}
		
		guiFluidStacks.init(inputTank, true, 141, 1, 16, 58, 10000, false, tankOverlay);

		ICarpenterRecipe recipe = recipeWrapper.getRecipe();
		ItemStack box = recipe.getBox();
		if (box != null) {
			guiItemStacks.set(boxSlot, box);
		}

		List<ItemStack> outputs = ingredients.getOutputs(ItemStack.class);
		craftingGridHelper.setOutput(guiItemStacks, outputs);

		IDescriptiveRecipe craftingGridRecipe = recipe.getCraftingGridRecipe();

		List<Object> inputs = Arrays.asList(craftingGridRecipe.getIngredients());
		IStackHelper stackHelper = FactoryJeiPlugin.jeiHelpers.getStackHelper();
		List<List<ItemStack>> craftInputs = stackHelper.expandRecipeItemStackInputs(inputs);
		craftingGridHelper.setInputStacks(guiItemStacks, craftInputs, craftingGridRecipe.getWidth(), craftingGridRecipe.getHeight());

		List<List<FluidStack>> fluidInputs = ingredients.getInputs(FluidStack.class);
		if (!fluidInputs.isEmpty()) {
			guiFluidStacks.set(inputTank, fluidInputs.get(0));
		}
	}

}
