/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.factory.recipes.nei;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.fluids.FluidHelper;
import forestry.core.recipes.nei.NEIUtils;
import forestry.core.recipes.nei.PositionedFluidTank;
import forestry.core.recipes.nei.PositionedStackAdv;
import forestry.core.recipes.nei.RecipeHandlerBase;
import forestry.factory.gui.GuiSqueezer;
import forestry.factory.recipes.ISqueezerContainerRecipe;
import forestry.factory.recipes.SqueezerRecipeManager;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;

public class NEIHandlerSqueezer extends RecipeHandlerBase {

	private static final int[][] INPUTS = new int[][]{{0, 0}, {1, 0}, {2, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {1, 2}, {2, 2}};

	public class CachedSqueezerRecipe extends CachedBaseRecipe {

		public List<PositionedStack> inputs = new ArrayList<>();
		public PositionedFluidTank tank;
		public PositionedStackAdv remnants = null;
		public int processingTime;
		public boolean containerRecipe = false;

		public CachedSqueezerRecipe(ISqueezerRecipe recipe, boolean genPerms) {
			if (recipe.getResources() != null) {
				this.setIngredients(recipe.getResources());
			}
			if (recipe.getFluidOutput() != null) {
				setTankFluid(recipe.getFluidOutput());
			}
			if (recipe.getRemnants() != null) {
				this.remnants = new PositionedStackAdv(recipe.getRemnants(), 92, 49).setChance(recipe.getRemnantsChance());
			}

			this.processingTime = recipe.getProcessingTime();

			if (genPerms) {
				this.generatePermutations();
			}
		}

		public CachedSqueezerRecipe(ISqueezerRecipe recipe) {
			this(recipe, false);
		}

		public CachedSqueezerRecipe(ISqueezerContainerRecipe recipe, boolean genPerms) {
			this.containerRecipe = true;

			ItemStack emptyContainer = recipe.getEmptyContainer();
			List<ItemStack> ingredients = FluidHelper.getAllFilledContainers(emptyContainer);
			setIngredients(new Object[]{ingredients});
			List<FluidStack> fluids = new ArrayList<>();
			for (ItemStack ingredient : ingredients) {
				FluidStack fluidStack = FluidHelper.getFluidStackInContainer(ingredient);
				fluids.add(fluidStack);
			}
			setTankFluid(fluids);

			if (recipe.getRemnants() != null) {
				this.remnants = new PositionedStackAdv(recipe.getRemnants(), 92, 49).setChance(recipe.getRemnantsChance());
			}

			this.processingTime = recipe.getProcessingTime();

			if (genPerms) {
				this.generatePermutations();
			}
		}

		public void setIngredients(Object[] inputs) {
			this.inputs.clear();
			int i = 0;
			for (Object stack : inputs) {
				if (i >= INPUTS.length) {
					return;
				}
				this.inputs.add(new PositionedStack(stack, 12 + INPUTS[i][0] * 18, 10 + INPUTS[i][1] * 18, false));
				i++;
			}
		}

		public void setTankFluid(FluidStack... fluidStack) {
			setTankFluid(Arrays.asList(fluidStack));
		}

		public void setTankFluid(Collection<FluidStack> fluidStacks) {
			this.tank = new PositionedFluidTank(fluidStacks, 10000, new Rectangle(117, 7, 16, 58), NEIHandlerSqueezer.this.getGuiTexture(), new Point(176, 0));
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return this.getCycledIngredients(NEIHandlerSqueezer.this.cycleticks / 20, this.inputs);
		}

		@Override
		public List<PositionedStack> getCycledIngredients(int cycle, List<PositionedStack> ingredients) {
			if (containerRecipe) {
				// cycle ingredients and output together
				Random random = new Random(cycle);
				cycle = Math.abs(random.nextInt());
				for (PositionedStack ingredient : ingredients) {
					ingredient.setPermutationToRender(cycle % ingredient.items.length);
				}
				tank.setPermutationToRender(cycle % tank.getPermutationCount());
				return ingredients;
			} else {
				return super.getCycledIngredients(cycle, ingredients);
			}
		}

		@Override
		public PositionedFluidTank getFluidTank() {
			return this.tank;
		}

		@Override
		public PositionedStack getResult() {
			return this.remnants;
		}

		public void generatePermutations() {
			for (PositionedStack p : this.inputs) {
				p.generatePermutations();
			}
		}

	}

	@Override
	public String getRecipeID() {
		return "forestry.squeezer";
	}

	@Override
	public String getRecipeName() {
		return StatCollector.translateToLocal("tile.for.factory.5.name");
	}

	@Override
	public String getGuiTexture() {
		return "forestry:textures/gui/squeezersocket.png";
	}

	@Override
	public void loadTransferRects() {
		this.addTransferRect(98 - 16 - 6 - 4, 9 + 8 + 8 + 4, 15 + 16 + 6 + 4, 18);
	}

	@Override
	public void drawExtras(int recipeIndex) {
		CachedRecipe recipe = arecipes.get(recipeIndex);
		if (recipe instanceof CachedSqueezerRecipe) {
			int processingTime = ((CachedSqueezerRecipe) recipe).processingTime;
			this.drawProgressBar(70, 30, 176, 60, 43, 18, processingTime * 5, 0);
		}
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiSqueezer.class;
	}

	@Override
	public void loadAllRecipes() {
		for (ISqueezerRecipe recipe : RecipeManagers.squeezerManager.recipes()) {
			this.arecipes.add(new CachedSqueezerRecipe(recipe, true));
		}
		for (ISqueezerContainerRecipe containerRecipe : SqueezerRecipeManager.containerRecipes.values()) {
			this.arecipes.add(new CachedSqueezerRecipe(containerRecipe, true));
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (ISqueezerRecipe recipe : RecipeManagers.squeezerManager.recipes()) {
			if (NEIServerUtils.areStacksSameTypeCrafting(recipe.getRemnants(), result)) {
				this.arecipes.add(new CachedSqueezerRecipe(recipe, true));
			}
		}
		for (ISqueezerContainerRecipe containerRecipe : SqueezerRecipeManager.containerRecipes.values()) {
			if (NEIServerUtils.areStacksSameTypeCrafting(containerRecipe.getRemnants(), result)) {
				this.arecipes.add(new CachedSqueezerRecipe(containerRecipe, true));
			}
		}
	}

	@Override
	public void loadCraftingRecipes(FluidStack result) {
		for (ISqueezerRecipe recipe : RecipeManagers.squeezerManager.recipes()) {
			if (NEIUtils.areFluidsSameType(recipe.getFluidOutput(), result)) {
				this.arecipes.add(new CachedSqueezerRecipe(recipe, true));
			}
		}
		for (ISqueezerContainerRecipe containerRecipe : SqueezerRecipeManager.containerRecipes.values()) {
			ItemStack emptyContainer = containerRecipe.getEmptyContainer();
			ItemStack filledContainer = FluidHelper.getFilledContainer(result, emptyContainer);
			if (filledContainer != null) {
				ISqueezerRecipe recipe = containerRecipe.getSqueezerRecipe(filledContainer);
				if (recipe != null) {
					this.arecipes.add(new CachedSqueezerRecipe(recipe, true));
				}
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingred) {
		ingred.stackSize = 1;

		ISqueezerContainerRecipe containerRecipe = SqueezerRecipeManager.findMatchingContainerRecipe(ingred);
		if (containerRecipe != null) {
			CachedSqueezerRecipe crecipe = new CachedSqueezerRecipe(containerRecipe, true);
			crecipe.setIngredients(new ItemStack[]{ingred});
			FluidStack fluidStack = FluidHelper.getFluidStackInContainer(ingred);
			crecipe.setTankFluid(fluidStack);
			this.arecipes.add(crecipe);
			return;
		}

		ISqueezerRecipe recipe = SqueezerRecipeManager.findRecipeWithIngredient(ingred);
		if (recipe != null) {
			CachedSqueezerRecipe crecipe = new CachedSqueezerRecipe(recipe);
			this.arecipes.add(crecipe);
		}
	}

}
