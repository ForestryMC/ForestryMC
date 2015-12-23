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
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IVariableFermentable;
import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Constants;
import forestry.core.recipes.nei.NEIUtils;
import forestry.core.recipes.nei.PositionedFluidTank;
import forestry.core.recipes.nei.RecipeHandlerBase;
import forestry.factory.gui.GuiFermenter;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;

public class NEIHandlerFermenter extends RecipeHandlerBase {

	private static Class<? extends GuiContainer> guiClass;

	@Override
	public void prepare() {
		guiClass = GuiFermenter.class;
		API.setGuiOffset(guiClass, 5, 15);
	}

	public class CachedFermenterRecipe extends CachedBaseRecipe {

		public List<PositionedFluidTank> tanks = new ArrayList<>();
		public PositionedStack resource;
		public List<PositionedStack> inputItems = new ArrayList<>();

		public CachedFermenterRecipe(IFermenterRecipe recipe, ItemStack fermentable, boolean genPerms) {
			if (recipe.getFluidResource() != null) {
				FluidStack input = recipe.getFluidResource().copy();
				input.amount = recipe.getFermentationValue();
				this.tanks.add(new PositionedFluidTank(input, 10000, new Rectangle(30, 4, 16, 58), NEIHandlerFermenter.this.getGuiTexture(), new Point(176, 0)));
			}
			if (recipe.getOutput() != null) {
				int amount = Math.round(recipe.getFermentationValue() * recipe.getModifier());
				if (fermentable.getItem() instanceof IVariableFermentable) {
					amount *= ((IVariableFermentable) fermentable.getItem()).getFermentationModifier(fermentable);
				}
				FluidStack output = new FluidStack(recipe.getOutput(), amount);
				this.tanks.add(new PositionedFluidTank(output, 10000, new Rectangle(120, 4, 16, 58), NEIHandlerFermenter.this.getGuiTexture(), new Point(176, 0)));
			}

			this.inputItems.add(new PositionedStack(fermentable, 80, 8));
			List<ItemStack> fuels = new ArrayList<>();
			for (FermenterFuel fuel : FuelManager.fermenterFuel.values()) {
				fuels.add(fuel.item);
			}
			this.inputItems.add(new PositionedStack(fuels, 70, 42));

			if (genPerms) {
				this.generatePermutations();
			}
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return this.getCycledIngredients(NEIHandlerFermenter.this.cycleticks / 40, this.inputItems);
		}

		@Override
		public PositionedStack getResult() {
			return null;
		}

		@Override
		public List<PositionedFluidTank> getFluidTanks() {
			return this.tanks;
		}

		public void generatePermutations() {
			for (PositionedStack p : this.inputItems) {
				p.generatePermutations();
			}
		}

	}

	@Override
	public String getRecipeID() {
		return "forestry.fermenter";
	}

	@Override
	public String getRecipeName() {
		return StatCollector.translateToLocal("tile.for.factory.3.name");
	}

	@Override
	public String getGuiTexture() {
		return "forestry:textures/gui/fermenter.png";
	}

	@Override
	public void loadTransferRects() {
		this.addTransferRect(76, 27, 14, 12);
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return guiClass;
	}

	@Override
	public void drawBackground(int recipe) {
		this.changeToGuiTexture();
		GuiDraw.drawTexturedModalRect(25, 0, 30, 15, 116, 65);
	}

	@Override
	public void drawExtras(int recipe) {
		this.drawProgressBar(69, 17, 176, 60, 4, 18, 40, 11);
		this.drawProgressBar(93, 31, 176, 78, 4, 18, 80, 11);
	}

	private List<CachedFermenterRecipe> getCachedRecipes(IFermenterRecipe recipe, boolean generatePermutations) {
		if (recipe.getResource() != null && recipe.getResource().getItem() instanceof IVariableFermentable) {
			List<CachedFermenterRecipe> crecipes = new ArrayList<>();
			for (ItemStack stack : NEIUtils.getItemVariations(recipe.getResource())) {
				crecipes.add(new CachedFermenterRecipe(recipe, stack, generatePermutations));
			}
			return crecipes;
		}
		return Collections.singletonList(new CachedFermenterRecipe(recipe, recipe.getResource(), generatePermutations));
	}

	@Override
	public void loadAllRecipes() {
		for (IFermenterRecipe recipe : RecipeManagers.fermenterManager.recipes()) {
			this.arecipes.addAll(this.getCachedRecipes(recipe, true));
		}
	}

	@Override
	public void loadCraftingRecipes(FluidStack result) {
		for (IFermenterRecipe recipe : RecipeManagers.fermenterManager.recipes()) {
			FluidStack output = new FluidStack(recipe.getOutput(), Constants.BUCKET_VOLUME);
			if (NEIUtils.areFluidsSameType(output, result)) {
				this.arecipes.addAll(this.getCachedRecipes(recipe, true));
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingred) {
		super.loadUsageRecipes(ingred);
		for (IFermenterRecipe recipe : RecipeManagers.fermenterManager.recipes()) {
			if (recipe.getResource() != null) {
				for (ItemStack stack : NEIUtils.getItemVariations(recipe.getResource())) {
					if (stack.hasTagCompound() && NEIServerUtils.areStacksSameType(stack, ingred) || !stack.hasTagCompound() && NEIServerUtils.areStacksSameTypeCrafting(stack, ingred)) {
						CachedFermenterRecipe crecipe = new CachedFermenterRecipe(recipe, stack, true);
						this.arecipes.add(crecipe);
					}
				}
			}
			for (FermenterFuel fuel : FuelManager.fermenterFuel.values()) {
				if (NEIServerUtils.areStacksSameTypeCrafting(fuel.item, ingred)) {
					for (CachedFermenterRecipe crecipe : this.getCachedRecipes(recipe, true)) {
						crecipe.setIngredientPermutation(crecipe.inputItems, ingred);
						this.arecipes.add(crecipe);
					}
				}
			}
		}
	}

	@Override
	public void loadUsageRecipes(FluidStack ingred) {
		for (IFermenterRecipe recipe : RecipeManagers.fermenterManager.recipes()) {
			if (NEIUtils.areFluidsSameType(recipe.getFluidResource(), ingred)) {
				this.arecipes.addAll(this.getCachedRecipes(recipe, true));
			}
		}
	}

}
