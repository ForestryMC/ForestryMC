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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.recipes.nei.NEIUtils;
import forestry.core.recipes.nei.PositionedFluidTank;
import forestry.core.recipes.nei.RecipeHandlerBase;
import forestry.factory.gui.GuiFabricator;
import forestry.factory.recipes.FabricatorSmeltingRecipeManager;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;

public class NEIHandlerFabricator extends RecipeHandlerBase {

	public class CachedFabricatorRecipe extends CachedBaseRecipe {
		public List<PositionedStack> smeltingInput = new ArrayList<>();
		public PositionedFluidTank tank;
		public List<PositionedStack> inputs = new ArrayList<>();
		public PositionedStack output;

		public CachedFabricatorRecipe(IFabricatorRecipe recipe, boolean genPerms) {
			if (recipe.getLiquid() != null) {
				this.tank = new PositionedFluidTank(recipe.getLiquid(), 2000, new Rectangle(21, 37, 16, 16));
				List<ItemStack> smeltingInput = new ArrayList<>();
				for (IFabricatorSmeltingRecipe s : getSmeltingInputs().get(recipe.getLiquid().getFluid())) {
					smeltingInput.add(s.getResource());
				}
				if (!smeltingInput.isEmpty()) {
					this.smeltingInput.add(new PositionedStack(smeltingInput, 21, 10));
				}
			}

			if (recipe != null) {
				if (recipe.getIngredients() != null) {
					this.setIngredients(recipe.getWidth(), recipe.getHeight(), recipe.getIngredients());
				}
				if (recipe.getPlan() != null) {
					this.inputs.add(new PositionedStack(recipe.getPlan(), 134, 6));
				}

				if (recipe.getRecipeOutput() != null) {
					this.output = new PositionedStack(recipe.getRecipeOutput(), 134, 42);
				}
			}

			if (genPerms) {
				this.generatePermutations();
			}
		}

		public CachedFabricatorRecipe(IFabricatorRecipe recipe) {
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

					PositionedStack stack = new PositionedStack(item, 62 + x * 18, 6 + y * 18, false);
					stack.setMaxSize(1);
					this.inputs.add(stack);
				}
			}
		}

		@Override
		public PositionedStack getResult() {
			return this.output;
		}

		@Override
		public List<PositionedStack> getOtherStacks() {
			return super.getCycledIngredients(NEIHandlerFabricator.this.cycleticks / 40, this.smeltingInput);
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return getCycledIngredients(NEIHandlerFabricator.this.cycleticks / 20, this.inputs);
		}

		@Override
		public PositionedFluidTank getFluidTank() {
			return this.tank;
		}

		public void generatePermutations() {
			for (PositionedStack p : this.inputs) {
				p.generatePermutations();
			}
		}

	}

	@Override
	public List<PositionedStack> getIngredientStacks(int recipe) {
		return arecipes.get(recipe).getIngredients();
	}

	@Override
	public PositionedStack getResultStack(int recipe) {
		return arecipes.get(recipe).getResult();
	}

	@Override
	public String getRecipeID() {
		return "forestry.fabricator";
	}

	@Override
	public String getRecipeName() {
		return StatCollector.translateToLocal("tile.for.factory2.0.name");
	}

	@Override
	public String getGuiTexture() {
		return "forestry:textures/gui/fabricator.png";
	}

	@Override
	public void loadTransferRects() {
		this.addTransferRect(117, 44, 14, 13);
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiFabricator.class;
	}

	@Override
	public void loadAllRecipes() {
		for (IFabricatorRecipe recipe : RecipeManagers.fabricatorManager.recipes()) {
			this.arecipes.add(new CachedFabricatorRecipe(recipe, true));
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (IFabricatorRecipe recipe : RecipeManagers.fabricatorManager.recipes()) {
			if (NEIServerUtils.areStacksSameTypeCrafting(recipe.getRecipeOutput(), result)) {
				CachedFabricatorRecipe crecipe = new CachedFabricatorRecipe(recipe, true);
				this.arecipes.add(crecipe);
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingred) {
		super.loadUsageRecipes(ingred);
		for (IFabricatorRecipe recipe : RecipeManagers.fabricatorManager.recipes()) {
			CachedFabricatorRecipe crecipe = new CachedFabricatorRecipe(recipe);
			if (crecipe.inputs != null && crecipe.contains(crecipe.inputs, ingred) || crecipe.smeltingInput != null && crecipe.contains(crecipe.smeltingInput, ingred)) {
				crecipe.generatePermutations();
				crecipe.setIngredientPermutation(crecipe.smeltingInput, ingred);
				this.arecipes.add(crecipe);
			}
		}
	}

	@Override
	public void loadUsageRecipes(FluidStack ingredient) {
		for (IFabricatorRecipe recipe : RecipeManagers.fabricatorManager.recipes()) {
			if (NEIUtils.areFluidsSameType(recipe.getLiquid(), ingredient)) {
				this.arecipes.add(new CachedFabricatorRecipe(recipe, true));
			}
		}
	}

	@Override
	public List<String> provideItemTooltip(GuiRecipe guiRecipe, ItemStack itemStack, List<String> currenttip, CachedBaseRecipe crecipe, Point relMouse) {
		super.provideItemTooltip(guiRecipe, itemStack, currenttip, crecipe, relMouse);

		if (new Rectangle(20, 9, 18, 18).contains(relMouse)) {
			for (IFabricatorSmeltingRecipe smelting : FabricatorSmeltingRecipeManager.recipes) {
				if (NEIServerUtils.areStacksSameTypeCrafting(smelting.getResource(), itemStack) && smelting.getProduct() != null) {
					currenttip.add(EnumChatFormatting.GRAY + NEIUtils.translate("handler.forestry.fabricator.worth") + " " + smelting.getProduct().amount + " mB");
				}
			}
		}

		return currenttip;
	}

	private static Map<Fluid, List<IFabricatorSmeltingRecipe>> getSmeltingInputs() {
		Map<Fluid, List<IFabricatorSmeltingRecipe>> smeltingInputs = new HashMap<>();
		for (IFabricatorSmeltingRecipe smelting : FabricatorSmeltingRecipeManager.recipes) {
			Fluid fluid = smelting.getProduct().getFluid();
			if (!smeltingInputs.containsKey(fluid)) {
				smeltingInputs.put(fluid, new ArrayList<IFabricatorSmeltingRecipe>());
			}
			smeltingInputs.get(fluid).add(smelting);
		}
		return smeltingInputs;
	}

}
