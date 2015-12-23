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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.recipes.nei.PositionedStackAdv;
import forestry.core.recipes.nei.RecipeHandlerBase;
import forestry.factory.gui.GuiCentrifuge;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;

public class NEIHandlerCentrifuge extends RecipeHandlerBase {

	private static final int[][] OUTPUTS = new int[][]{{0, 0}, {1, 0}, {2, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {1, 2}, {2, 2}};

	private static final Comparator<Entry<ItemStack, Float>> highestChanceComparator = new Comparator<Entry<ItemStack, Float>>() {
		@Override
		public int compare(Entry<ItemStack, Float> o1, Entry<ItemStack, Float> o2) {
			return o2.getValue().compareTo(o1.getValue());
		}
	};

	public class CachedCentrifugeRecipe extends CachedBaseRecipe {

		public PositionedStack inputs;
		public List<PositionedStack> outputs = new ArrayList<>();

		public CachedCentrifugeRecipe(ICentrifugeRecipe recipe, boolean genPerms) {
			if (recipe.getInput() != null) {
				this.inputs = new PositionedStack(recipe.getInput(), 25, 26);
			}
			if (recipe.getAllProducts() != null) {
				this.setResults(recipe.getAllProducts());
			}
		}

		public CachedCentrifugeRecipe(ICentrifugeRecipe recipe) {
			this(recipe, false);
		}

		public void setResults(Map<ItemStack, Float> outputs) {
			Set<Entry<ItemStack, Float>> entrySet = outputs.entrySet();
			if (entrySet.size() == 0) {
				return;
			}
			PriorityQueue<Entry<ItemStack, Float>> sortByChance = new PriorityQueue<>(entrySet.size(), highestChanceComparator);
			sortByChance.addAll(entrySet);

			int i = 0;
			while (!sortByChance.isEmpty()) {
				Entry<ItemStack, Float> stack = sortByChance.poll();
				if (i >= OUTPUTS.length) {
					return;
				}
				PositionedStackAdv output = new PositionedStackAdv(stack.getKey(), 93 + OUTPUTS[i][0] * 18, 8 + OUTPUTS[i][1] * 18);
				output.setChance(stack.getValue());
				this.outputs.add(output);
				i++;
			}
		}

		@Override
		public PositionedStack getIngredient() {
			this.randomRenderPermutation(this.inputs, NEIHandlerCentrifuge.this.cycleticks / 20);
			return this.inputs;
		}

		@Override
		public List<PositionedStack> getOtherStacks() {
			return this.outputs;
		}

		@Override
		public PositionedStack getResult() {
			return null;
		}

	}

	@Override
	public String getRecipeID() {
		return "forestry.centrifuge";
	}

	@Override
	public String getRecipeName() {
		return StatCollector.translateToLocal("tile.for.factory.2.name");
	}

	@Override
	public String getGuiTexture() {
		return "forestry:textures/gui/centrifugesocket.png";
	}

	@Override
	public void loadTransferRects() {
		this.addTransferRect(57 - 12, 26 - 8, 16, 24 + 6 + 6);
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiCentrifuge.class;
	}

	@Override
	public void drawExtras(int recipe) {
		this.drawProgressBar(57 - 4, 25, 176, 0, 4, 17, 80, 3);
	}

	@Override
	public void loadAllRecipes() {
		for (ICentrifugeRecipe recipe : RecipeManagers.centrifugeManager.recipes()) {
			this.arecipes.add(new CachedCentrifugeRecipe(recipe, true));
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (ICentrifugeRecipe recipe : RecipeManagers.centrifugeManager.recipes()) {
			CachedCentrifugeRecipe crecipe = new CachedCentrifugeRecipe(recipe);
			if (crecipe.outputs != null && crecipe.contains(crecipe.outputs, result)) {
				crecipe.setIngredientPermutation(crecipe.outputs, result);
				this.arecipes.add(crecipe);
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingred) {
		super.loadCraftingRecipes(ingred);
		for (ICentrifugeRecipe recipe : RecipeManagers.centrifugeManager.recipes()) {
			if (NEIServerUtils.areStacksSameTypeCrafting(recipe.getInput(), ingred)) {
				this.arecipes.add(new CachedCentrifugeRecipe(recipe, true));
			}
		}
	}

}
