package forestry.factory.recipes.jei.moistener;

import javax.annotation.Nonnull;

import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

public class MoistenerRecipeCategory extends ForestryRecipeCategory {

	private static final int resourceSlot = 0;
	private static final int productSlot = 1;
	private static final int fuelItemSlot = 2;
	private static final int fuelProductSlot = 3;
	
	private static final int inputTank = 0;
	
	private final static ResourceLocation guiTexture = new ResourceLocation("forestry", "textures/gui/moistener.png");
	@Nonnull
	protected final IDrawableAnimated arrow;
	@Nonnull
	protected final IDrawableAnimated progressBar;
	@Nonnull
	protected final IDrawable tankOverlay;
	
	public MoistenerRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 9, 16, 158, 61), "tile.for.factory.4.name");
		
		IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 91, 29, 55);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 80, IDrawableAnimated.StartDirection.BOTTOM, false);
		IDrawableStatic progressBar = guiHelper.createDrawable(guiTexture, 176, 74, 16, 15);
		this.progressBar = guiHelper.createAnimatedDrawable(progressBar, 160, IDrawableAnimated.StartDirection.BOTTOM, false);
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
	}
	
	@Nonnull
	@Override
	public String getUid() {
		return ForestryRecipeCategoryUid.MOISTENER;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {	
	}

	@Override
	public void drawAnimations(Minecraft minecraft) {
		arrow.draw(minecraft, 88, 6);
		progressBar.draw(minecraft, 119, 25);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		
		guiItemStacks.init(resourceSlot, true, 138, 8);
		guiItemStacks.init(fuelItemSlot, true, 34, 47);
		
		guiItemStacks.init(productSlot, false, 138, 44);
		guiItemStacks.init(fuelProductSlot, false, 100, 26);
		
		guiFluidStacks.init(inputTank, true, 11, 5, 16, 58, 10000, false, tankOverlay);
		
		MoistenerRecipeWrapper wrapper = (MoistenerRecipeWrapper) recipeWrapper;
		guiItemStacks.set(resourceSlot, wrapper.getRecipe().getResource());
		guiItemStacks.set(fuelItemSlot, wrapper.getFuel().item);
		
		guiItemStacks.set(productSlot, wrapper.getRecipe().getProduct());
		guiItemStacks.set(fuelProductSlot, wrapper.getFuel().product);
		
		guiFluidStacks.set(inputTank, FluidRegistry.getFluidStack("water", 10000));
		
	}

}
