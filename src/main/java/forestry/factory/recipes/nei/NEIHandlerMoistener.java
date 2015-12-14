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

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.recipes.nei.PositionedFluidTank;
import forestry.core.recipes.nei.RecipeHandlerBase;
import forestry.factory.gui.GuiMoistener;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;

public class NEIHandlerMoistener extends RecipeHandlerBase {

	public class CachedMoistenerRecipe extends CachedBaseRecipe {

		public PositionedFluidTank tank;
		public List<PositionedStack> fuels = new ArrayList<>();
		public PositionedStack input;
		public PositionedStack output;

		public CachedMoistenerRecipe(IMoistenerRecipe recipe, MoistenerFuel fuel) {
			this.tank = new PositionedFluidTank(FluidRegistry.getFluidStack("water", 10000), 10000, new Rectangle(11, 5, 16, 58), NEIHandlerMoistener.this.getGuiTexture(), new Point(176, 0));
			this.tank.showAmount = false;
			if (fuel.item != null) {
				this.fuels.add(new PositionedStack(fuel.item, 34, 47));
			}
			if (fuel.product != null) {
				this.fuels.add(new PositionedStack(fuel.product, 100, 26));
			}
			if (recipe.getResource() != null) {
				this.input = new PositionedStack(recipe.getResource(), 138, 8);
			}
			if (recipe.getProduct() != null) {
				this.output = new PositionedStack(recipe.getProduct(), 138, 44);
			}
		}

		@Override
		public List<PositionedStack> getOtherStacks() {
			return this.fuels;
		}

		@Override
		public PositionedStack getIngredient() {
			return this.input;
		}

		@Override
		public PositionedStack getResult() {
			return this.output;
		}

		@Override
		public PositionedFluidTank getFluidTank() {
			return this.tank;
		}

	}

	@Override
	public String getRecipeID() {
		return "forestry.moistener";
	}

	@Override
	public String getRecipeName() {
		return StatCollector.translateToLocal("tile.for.factory.4.name");
	}

	@Override
	public String getGuiTexture() {
		return "forestry:textures/gui/moistener.png";
	}

	@Override
	public void loadTransferRects() {
		this.addTransferRect(138, 27, 16, 14);
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiMoistener.class;
	}

	@Override
	public void drawBackground(int recipe) {
		super.drawBackground(recipe);
		this.drawProgressBar(88, 6, 176, 91, 29, 55, 80, 3);
	}

	@Override
	public void drawExtras(int recipe) {
		this.drawProgressBar(119, 25, 176, 74, 16, 15, 160, 0);
	}

	@Override
	public void loadAllRecipes() {
		for (IMoistenerRecipe recipe : RecipeManagers.moistenerManager.recipes()) {
			for (MoistenerFuel fuel : FuelManager.moistenerResource.values()) {
				this.arecipes.add(new CachedMoistenerRecipe(recipe, fuel));
			}
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (IMoistenerRecipe recipe : RecipeManagers.moistenerManager.recipes()) {
			for (MoistenerFuel fuel : FuelManager.moistenerResource.values()) {
				if (NEIServerUtils.areStacksSameTypeCrafting(recipe.getProduct(), result) || NEIServerUtils.areStacksSameTypeCrafting(fuel.product, result)) {
					this.arecipes.add(new CachedMoistenerRecipe(recipe, fuel));
				}
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingred) {
		super.loadUsageRecipes(ingred);
		for (IMoistenerRecipe recipe : RecipeManagers.moistenerManager.recipes()) {
			for (MoistenerFuel fuel : FuelManager.moistenerResource.values()) {
				if (NEIServerUtils.areStacksSameTypeCrafting(recipe.getResource(), ingred) || NEIServerUtils.areStacksSameTypeCrafting(fuel.item, ingred)) {
					this.arecipes.add(new CachedMoistenerRecipe(recipe, fuel));
				}
			}
		}
	}

	@Override
	public void loadUsageRecipes(FluidStack ingredient) {
		if (ingredient.getFluid() == FluidRegistry.WATER) {
			this.loadAllRecipes();
		}
	}

}
