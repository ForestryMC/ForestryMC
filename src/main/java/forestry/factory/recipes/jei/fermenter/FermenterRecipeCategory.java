package forestry.factory.recipes.jei.fermenter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeCategoryUid;
import forestry.core.render.ForestryResource;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;

public class FermenterRecipeCategory extends ForestryRecipeCategory<FermenterRecipeWrapper> {

	private static final int resourceSlot = 0;
	private static final int fuelSlot = 1;
	
	private static final int inputTank = 0;
	private static final int outputTank = 1;
	
	private final static ResourceLocation guiTexture = new ForestryResource("textures/gui/fermenter.png");
	@Nonnull
	private final IDrawableAnimated progressBar0;
	@Nonnull
	private final IDrawableAnimated progressBar1;
	@Nonnull
	private final IDrawable tankOverlay;
	
	public FermenterRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createDrawable(guiTexture, 34, 18, 108, 60), "tile.for.fermenter.name");
		
		IDrawableStatic progressBarDrawable0 = guiHelper.createDrawable(guiTexture, 176, 60, 4, 18);
		this.progressBar0 = guiHelper.createAnimatedDrawable(progressBarDrawable0, 40, IDrawableAnimated.StartDirection.BOTTOM, false);
		IDrawableStatic progressBarDrawable1 = guiHelper.createDrawable(guiTexture, 176, 78, 4, 18);
		this.progressBar1 = guiHelper.createAnimatedDrawable(progressBarDrawable1, 80, IDrawableAnimated.StartDirection.BOTTOM, false);
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 192, 0, 16, 58);
	}
	
	@Nonnull
	@Override
	public String getUid() {
		return ForestryRecipeCategoryUid.FERMENTER;
	}

	@Override
	public void drawAnimations(@Nonnull Minecraft minecraft) {
		progressBar0.draw(minecraft, 40, 14);
		progressBar1.draw(minecraft, 64, 28);
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull FermenterRecipeWrapper recipeWrapper) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		
		guiItemStacks.init(resourceSlot, true, 50, 4);
		guiItemStacks.init(fuelSlot, true, 40, 38);
		
		guiFluidStacks.init(inputTank, true, 1, 1, 16, 58, 3000, false, tankOverlay);
		guiFluidStacks.init(outputTank, false, 91, 1, 16, 58, 3000, false, tankOverlay);
		
		guiItemStacks.set(resourceSlot, recipeWrapper.getFermentable());
		List<ItemStack> fuels = new ArrayList<>();
		for (FermenterFuel fuel : FuelManager.fermenterFuel.values()) {
			fuels.add(fuel.item);
		}
		guiItemStacks.set(fuelSlot, fuels);
		
		guiFluidStacks.set(inputTank, recipeWrapper.getFluidInputs());
		guiFluidStacks.set(outputTank, recipeWrapper.getFluidOutputs());
	}

}
