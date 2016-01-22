package forestry.factory.recipes.jei.fermenter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FermenterRecipeCategory extends ForestryRecipeCategory {

	private static final int resourceSlot = 0;
	private static final int fuelSlot = 1;
	
	private static final int inputTank = 0;
	private static final int outputTank = 1;
	
	private final static ResourceLocation guiTexture = new ResourceLocation("forestry", "textures/gui/fermenter.png");
	@Nonnull
	protected final IDrawableAnimated progressBar0;
	@Nonnull
	protected final IDrawableAnimated progressBar1;
	@Nonnull
	protected final IDrawable tankOverlay;
	
	public FermenterRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 30, 15, 116, 65), "tile.for.factory.3.name");
		
		IDrawableStatic progressBarDrawable0 = guiHelper.createDrawable(guiTexture, 176, 60, 4, 18);
		this.progressBar0 = guiHelper.createAnimatedDrawable(progressBarDrawable0, 40, IDrawableAnimated.StartDirection.BOTTOM, false);
		IDrawableStatic progressBarDrawable1 = guiHelper.createDrawable(guiTexture, 176, 78, 4, 18);
		this.progressBar1 = guiHelper.createAnimatedDrawable(progressBarDrawable1, 80, IDrawableAnimated.StartDirection.BOTTOM, false);
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
	}
	
	@Nonnull
	@Override
	public String getUid() {
		return ForestryRecipeCategoryUid.FERMENTER;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {	
	}

	@Override
	public void drawAnimations(Minecraft minecraft) {
		progressBar0.draw(minecraft, 44, 17);
		progressBar1.draw(minecraft, 68, 31);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		
		guiItemStacks.init(resourceSlot, true, 54, 7);
		guiItemStacks.init(fuelSlot, true, 44, 41);
		
		guiFluidStacks.init(inputTank, true, 5, 4, 16, 58, 10000, false, tankOverlay);
		guiFluidStacks.init(outputTank, false, 95, 4, 16, 58, 10000, false, tankOverlay);
		
		FermenterRecipeWrapper wrapper = (FermenterRecipeWrapper) recipeWrapper;
		
		guiItemStacks.set(resourceSlot, wrapper.getFermentable());
		List<ItemStack> fuels = new ArrayList<>();
		for (FermenterFuel fuel : FuelManager.fermenterFuel.values()) {
			fuels.add(fuel.item);
		}
		guiItemStacks.set(fuelSlot, fuels);
		
		guiFluidStacks.set(inputTank, wrapper.getFluidInputs());
		guiFluidStacks.set(outputTank, wrapper.getFluidOutputs());
		
	}

}
