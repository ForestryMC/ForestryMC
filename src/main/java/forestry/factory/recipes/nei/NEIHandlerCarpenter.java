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
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.recipes.nei.NEIUtils;
import forestry.core.recipes.nei.PositionedFluidTank;
import forestry.core.recipes.nei.RecipeHandlerBase;
import forestry.factory.gui.GuiCarpenter;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;

public class NEIHandlerCarpenter extends RecipeHandlerBase {

	private static Class<? extends GuiContainer> guiClass;

	@Override
	public void prepare() {
		guiClass = GuiCarpenter.class;
		API.setGuiOffset(guiClass, 5, 14);
	}

	public class CachedCarpenterRecipe extends CachedBaseRecipe {

		public List<PositionedStack> inputs = new ArrayList<>();
		public PositionedFluidTank tank;
		public PositionedStack output;

		public CachedCarpenterRecipe(ICarpenterRecipe recipe, boolean genPerms) {
			IDescriptiveRecipe irecipe = recipe.getCraftingGridRecipe();
			if (irecipe != null) {
				if (irecipe.getIngredients() != null) {
					this.setIngredients(irecipe.getWidth(), irecipe.getHeight(), irecipe.getIngredients());
				}
				if (recipe.getBox() != null) {
					this.inputs.add(new PositionedStack(recipe.getBox(), 78, 6));
				}
				if (recipe.getFluidResource() != null) {
					this.tank = new PositionedFluidTank(recipe.getFluidResource(), 10000, new Rectangle(145, 3, 16, 58), NEIHandlerCarpenter.this.getGuiTexture(), new Point(176, 0));
				}
				if (recipe.getCraftingGridRecipe().getRecipeOutput() != null) {
					this.output = new PositionedStack(recipe.getCraftingGridRecipe().getRecipeOutput(), 75, 37);
				}
			}

			if (genPerms) {
				this.generatePermutations();
			}
		}

		public CachedCarpenterRecipe(ICarpenterRecipe recipe) {
			this(recipe, false);
		}

		public void setIngredients(int width, int height, Object[] items) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					Object item = items[y * width + x];
					if (item == null) {
						continue;
					} else if (item instanceof ItemStack[] && ((ItemStack[]) item).length == 0) {
						continue;
					} else if (item instanceof List && ((List) item).size() == 0) {
						continue;
					}

					PositionedStack stack = new PositionedStack(item, 5 + x * 18, 6 + y * 18, false);
					stack.setMaxSize(1);
					this.inputs.add(stack);
				}
			}
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return this.getCycledIngredients(NEIHandlerCarpenter.this.cycleticks / 20, this.inputs);
		}

		@Override
		public PositionedFluidTank getFluidTank() {
			return this.tank;
		}

		@Override
		public PositionedStack getResult() {
			return this.output;
		}

		public void generatePermutations() {
			for (PositionedStack p : this.inputs) {
				p.generatePermutations();
			}
		}

	}

	@Override
	public String getRecipeID() {
		return "forestry.carpenter";
	}

	@Override
	public String getRecipeName() {
		return StatCollector.translateToLocal("tile.for.factory.1.name");
	}

	@Override
	public String getGuiTexture() {
		return "forestry:textures/gui/carpenter.png";
	}

	@Override
	public void loadTransferRects() {
		this.addTransferRect(93, 36, 4, 18);
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return guiClass;
	}

	@Override
	public void drawBackground(int recipe) {
		this.changeToGuiTexture();
		GuiDraw.drawTexturedModalRect(0, 0, 5, 14, 166, 65);
	}

	@Override
	public void drawExtras(int recipe) {
		this.drawProgressBar(93, 36, 176, 59, 4, 17, 80, 3);
	}

	@Override
	public void loadAllRecipes() {
		for (ICarpenterRecipe recipe : RecipeManagers.carpenterManager.recipes()) {
			this.arecipes.add(new CachedCarpenterRecipe(recipe, true));
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (ICarpenterRecipe recipe : RecipeManagers.carpenterManager.recipes()) {
			if (NEIServerUtils.areStacksSameTypeCrafting(recipe.getCraftingGridRecipe().getRecipeOutput(), result)) {
				this.arecipes.add(new CachedCarpenterRecipe(recipe, true));
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingred) {
		super.loadUsageRecipes(ingred);
		for (ICarpenterRecipe recipe : RecipeManagers.carpenterManager.recipes()) {
			CachedCarpenterRecipe crecipe = new CachedCarpenterRecipe(recipe);
			if (crecipe.inputs != null && crecipe.contains(crecipe.inputs, ingred)) {
				crecipe.generatePermutations();
				crecipe.setIngredientPermutation(crecipe.inputs, ingred);
				this.arecipes.add(crecipe);
			}
		}
	}

	@Override
	public void loadUsageRecipes(FluidStack ingredient) {
		for (ICarpenterRecipe recipe : RecipeManagers.carpenterManager.recipes()) {
			if (NEIUtils.areFluidsSameType(recipe.getFluidResource(), ingredient)) {
				this.arecipes.add(new CachedCarpenterRecipe(recipe, true));
			}
		}
	}

}
