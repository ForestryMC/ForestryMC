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
import net.minecraft.util.StatCollector;

import net.minecraftforge.fluids.FluidStack;

import forestry.api.recipes.IStillRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.recipes.nei.NEIUtils;
import forestry.core.recipes.nei.PositionedFluidTank;
import forestry.core.recipes.nei.RecipeHandlerBase;
import forestry.factory.gui.GuiStill;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;

public class NEIHandlerStill extends RecipeHandlerBase {

	public class CachedStillRecipe extends CachedBaseRecipe {

		public List<PositionedFluidTank> tanks = new ArrayList<>();

		public CachedStillRecipe(IStillRecipe recipe) {
			if (recipe.getInput() != null) {
				this.tanks.add(new PositionedFluidTank(recipe.getInput(), 10000, new Rectangle(30, 4, 16, 58), NEIHandlerStill.this.getGuiTexture(), new Point(176, 0)));
			}
			if (recipe.getOutput() != null) {
				this.tanks.add(new PositionedFluidTank(recipe.getOutput(), 10000, new Rectangle(120, 4, 16, 58), NEIHandlerStill.this.getGuiTexture(), new Point(176, 0)));
			}
		}

		@Override
		public PositionedStack getResult() {
			return null;
		}

		@Override
		public List<PositionedFluidTank> getFluidTanks() {
			return this.tanks;
		}

	}

	@Override
	public String getRecipeID() {
		return "forestry.still";
	}

	@Override
	public String getRecipeName() {
		return StatCollector.translateToLocal("tile.for.factory.6.name");
	}

	@Override
	public String getGuiTexture() {
		return "forestry:textures/gui/still.png";
	}

	@Override
	public void loadTransferRects() {
		this.addTransferRect(76, 27, 14, 12);
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiStill.class;
	}

	@Override
	public void drawBackground(int recipe) {
		this.changeToGuiTexture();
		GuiDraw.drawTexturedModalRect(25, 0, 30, 11, 116, 65);
	}

	@Override
	public void drawExtras(int recipe) {
		this.drawProgressBar(79, 6, 176, 74, 4, 18, 80, 11);
		GuiDraw.drawTexturedModalRect(77, 46, 176, 60, 14, 14);
	}

	@Override
	public void loadAllRecipes() {
		for (IStillRecipe recipe : RecipeManagers.stillManager.recipes()) {
			this.arecipes.add(new CachedStillRecipe(recipe));
		}
	}

	@Override
	public void loadCraftingRecipes(FluidStack result) {
		for (IStillRecipe recipe : RecipeManagers.stillManager.recipes()) {
			if (NEIUtils.areFluidsSameType(recipe.getOutput(), result)) {
				this.arecipes.add(new CachedStillRecipe(recipe));
			}
		}
	}

	@Override
	public void loadUsageRecipes(FluidStack ingred) {
		for (IStillRecipe recipe : RecipeManagers.stillManager.recipes()) {
			if (NEIUtils.areFluidsSameType(recipe.getInput(), ingred)) {
				this.arecipes.add(new CachedStillRecipe(recipe));
			}
		}
	}

}
