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
package forestry.core.recipes.nei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import forestry.core.recipes.ShapelessRecipeCustom;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;

public class NEIHandlerShapelessCustom extends RecipeHandlerBase {
	private static final int[][] OUTPUTS = new int[][]{{0, 0}, {1, 0}, {2, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {1, 2}, {2, 2}};

	public class CachedShapelessCustomRecipe extends CachedBaseRecipe {
		public List<PositionedStack> inputs = new ArrayList<>();
		public PositionedStack output;

		public CachedShapelessCustomRecipe(ShapelessRecipeCustom recipe) {
			if (recipe.getIngredients() != null && recipe.getIngredients().size() > 0) {
				this.setIngredients(recipe.getIngredients());
			}
			if (recipe.getRecipeOutput() != null) {
				ItemStack output = recipe.getRecipeOutput();
				this.output = new PositionedStack(output, 119, 24);
			}
		}

		public void setIngredients(List<ItemStack> items) {
			for (int i = 0; i < items.size(); i++) {
				ItemStack item = items.get(i);
				if (item == null) {
					continue;
				}

				PositionedStack stack = new PositionedStack(item, 25 + OUTPUTS[i][0] * 18, 6 + OUTPUTS[i][1] * 18);
				stack.setMaxSize(1);
				this.inputs.add(stack);
			}
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return this.getCycledIngredients(NEIHandlerShapelessCustom.this.cycleticks / 20, this.inputs);
		}

		@Override
		public PositionedStack getResult() {
			return this.output;
		}

	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (Object recipe : CraftingManager.getInstance().getRecipeList()) {
			if (recipe instanceof ShapelessRecipeCustom && NEIServerUtils.areStacksSameTypeCrafting(((ShapelessRecipeCustom) recipe).getRecipeOutput(), result)) {
				CachedShapelessCustomRecipe crecipe = new CachedShapelessCustomRecipe((ShapelessRecipeCustom) recipe);
				this.arecipes.add(crecipe);
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		for (Object recipe : CraftingManager.getInstance().getRecipeList()) {
			if (recipe instanceof ShapelessRecipeCustom) {
				CachedShapelessCustomRecipe crecipe = new CachedShapelessCustomRecipe((ShapelessRecipeCustom) recipe);
				if (crecipe.inputs != null && crecipe.contains(crecipe.inputs, ingredient)) {
					this.arecipes.add(crecipe);
				}
			}
		}
	}

	@Override
	public String getRecipeID() {
		return "crafting";
	}

	@Override
	public String getOverlayIdentifier() {
		return "crafting";
	}

	@Override
	public String getRecipeName() {
		return NEIUtils.translate("handler.forestry.shapeless");
	}

	@Override
	public String getGuiTexture() {
		return "minecraft:textures/gui/container/crafting_table.png";
	}
}
