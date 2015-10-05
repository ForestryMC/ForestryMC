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

import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;

import forestry.factory.gadgets.MachineBottler;
import forestry.factory.gui.GuiBottler;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;

public class NEIHandlerBottler extends RecipeHandlerBase {

	private static List<MachineBottler.Recipe> recipes = new ArrayList<MachineBottler.Recipe>();

	@Override
	public void prepare() {
		for (FluidContainerData container : FluidContainerRegistry.getRegisteredFluidContainerData()) {
			MachineBottler.Recipe recipe = MachineBottler.RecipeManager.findMatchingRecipe(container.fluid, container.emptyContainer);
			if (recipe != null) {
				recipes.add(recipe);
			}
		}
	}

	public class CachedBottlerRecipe extends CachedBaseRecipe {

		public PositionedFluidTank fluid;
		public PositionedStack input;
		public PositionedStack output;

		public CachedBottlerRecipe(MachineBottler.Recipe recipe) {
			if (recipe.input != null) {
				this.fluid = new PositionedFluidTank(recipe.input, 10000, new Rectangle(48, 6, 16, 58), NEIHandlerBottler.this.getGuiTexture(), new Point(176, 0));
			}
			if (recipe.can != null) {
				this.input = new PositionedStack(recipe.can, 111, 8);
			}
			if (recipe.bottled != null) {
				this.output = new PositionedStack(recipe.bottled, 111, 44);
			}
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
			return this.fluid;
		}

	}

	@Override
	public String getRecipeID() {
		return "forestry.bottler";
	}

	@Override
	public String getRecipeName() {
		return StatCollector.translateToLocal("tile.for.factory.0.name");
	}

	@Override
	public String getGuiTexture() {
		return "forestry:textures/gui/bottler.png";
	}

	@Override
	public void loadTransferRects() {
		this.addTransferRect(75, 27, 24, 17);
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiBottler.class;
	}

	@Override
	public void drawBackground(int recipe) {
		this.changeToGuiTexture();
		GuiDraw.drawTexturedModalRect(43, 0, 48, 11, 123, 65);
	}

	@Override
	public void drawExtras(int recipe) {
		this.drawProgressBar(75, 27, 176, 74, 24, 17, 40, 0);
	}

	@Override
	public void loadAllRecipes() {
		for (MachineBottler.Recipe recipe : recipes) {
			this.arecipes.add(new CachedBottlerRecipe(recipe));
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (MachineBottler.Recipe recipe : recipes) {
			if (NEIServerUtils.areStacksSameTypeCrafting(recipe.bottled, result)) {
				this.arecipes.add(new CachedBottlerRecipe(recipe));
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingred) {
		super.loadUsageRecipes(ingred);
		for (MachineBottler.Recipe recipe : recipes) {
			if (NEIServerUtils.areStacksSameTypeCrafting(recipe.can, ingred)) {
				this.arecipes.add(new CachedBottlerRecipe(recipe));
			}
		}
	}

	@Override
	public void loadUsageRecipes(FluidStack ingred) {
		for (MachineBottler.Recipe recipe : recipes) {
			if (NEIUtils.areFluidsSameType(recipe.input, ingred)) {
				this.arecipes.add(new CachedBottlerRecipe(recipe));
			}
		}
	}

}
