package forestry.factory.recipes.jei.squeezer;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import forestry.api.recipes.ISqueezerRecipe;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.recipes.jei.ForestryRecipeWrapper;
import forestry.factory.recipes.ISqueezerContainerRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
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

public class SqueezerRecipeCategory extends ForestryRecipeCategory {
	
	private static final int[][] INPUTS = new int[][]{{0, 0}, {1, 0}, {2, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {1, 2}, {2, 2}};
	
	private static final int craftOutputSlot = 0;
	private static final int craftInputSlot = 1;
	
	private static final int outputTank = 0;
	
	private final static ResourceLocation guiTexture = new ResourceLocation("forestry", "textures/gui/squeezersocket.png");
	@Nonnull
	protected final IDrawableAnimated arrow;
	@Nonnull
	protected final IDrawable tankOverlay;
	@Nonnull
	protected final String UID;
	
	public SqueezerRecipeCategory(IGuiHelper guiHelper, String UID) {
		super(guiHelper.createDrawable(guiTexture, 9, 16, 158, 61), "tile.for.factory.5.name", 10);
		
		IDrawableStatic arrowDrawable = guiHelper.createDrawable(guiTexture, 176, 60, 43, 18);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
		this.tankOverlay = guiHelper.createDrawable(guiTexture, 176, 0, 16, 58);
		this.UID = UID;
	}
	
	@Nonnull
	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {	
	}

	@Override
	public void drawAnimations(Minecraft minecraft) {
		arrow.draw(minecraft, 67, 25);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		super.setRecipe(recipeLayout, recipeWrapper);
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		
		ForestryRecipeWrapper wrapper = (ForestryRecipeWrapper) recipeWrapper;
		
		float chance = 0;
		if(wrapper.getRecipe() instanceof ISqueezerRecipe){
			chance = ((ISqueezerRecipe) wrapper.getRecipe()).getRemnantsChance();
		}else if(wrapper.getRecipe() instanceof ISqueezerContainerRecipe){
			chance = ((ISqueezerContainerRecipe) wrapper.getRecipe()).getRemnantsChance();
		}
		
		guiFluidStacks.init(outputTank, false, 113, 2, 16, 58, 10000, false, tankOverlay);
		guiFluidStacks.set(outputTank, recipeWrapper.getFluidOutputs());
		
		guiItemStacks.init(craftOutputSlot, false, 87, 43);
		guiItemStacks.set(craftOutputSlot, recipeWrapper.getOutputs());
		tooltip.addChanceTooltip(craftOutputSlot, chance);
		setIngredients(guiItemStacks, recipeWrapper.getInputs(), chance);

		guiItemStacks.addTooltipCallback(tooltip);
		
	}
	
	public void setIngredients(IGuiItemStackGroup guiItemStacks, List<Object> inputs, float chance) {
		int i = 0;
		for (Object stack : inputs) {
			guiItemStacks.init(craftInputSlot + i, true, 7 + INPUTS[i][0] * 18, 4 + INPUTS[i][1] * 18);
			if(stack instanceof ItemStack){
				guiItemStacks.set(craftInputSlot + i, (ItemStack) stack);
			}else{
				guiItemStacks.set(craftInputSlot + i, (Collection<ItemStack>) stack);
			}
			i++;
		}
	}

}
