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

import forestry.core.recipes.ShapedRecipeCustom;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;

public class NEIHandlerShapedCustom extends RecipeHandlerBase {

	public class CachedShapedCustomRecipe extends CachedBaseRecipe {
		public List<PositionedStack> inputs = new ArrayList<>();
		public PositionedStack output;

		public CachedShapedCustomRecipe(ShapedRecipeCustom recipe) {
			if (recipe.getIngredients() != null && recipe.getIngredients().length > 0) {
				this.setIngredients(recipe.getWidth(), recipe.getHeight(), recipe.getIngredients());
			}
			if (recipe.getRecipeOutput() != null) {
				ItemStack output = recipe.getRecipeOutput();
				this.output = new PositionedStack(output, 119, 24);
			}
		}

		public void setIngredients(int width, int height, Object[] items) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (items.length <= y * width + x) {
						continue;
					}

					Object item = items[y * width + x];
					if (item == null) {
						continue;
					} else if (item instanceof ItemStack[] && ((ItemStack[]) item).length == 0) {
						continue;
					} else if (item instanceof List && ((List) item).size() == 0) {
						continue;
					}

					PositionedStack stack = new PositionedStack(item, 25 + x * 18, 6 + y * 18);
					stack.setMaxSize(1);
					this.inputs.add(stack);
				}
			}
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return this.getCycledIngredients(NEIHandlerShapedCustom.this.cycleticks / 20, this.inputs);
		}

		@Override
		public PositionedStack getResult() {
			return this.output;
		}

	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (Object recipe : CraftingManager.getInstance().getRecipeList()) {
			if (recipe instanceof ShapedRecipeCustom && NEIServerUtils.areStacksSameTypeCrafting(((ShapedRecipeCustom) recipe).getRecipeOutput(), result)) {
				CachedShapedCustomRecipe crecipe = new CachedShapedCustomRecipe((ShapedRecipeCustom) recipe);
				this.arecipes.add(crecipe);
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		for (Object recipe : CraftingManager.getInstance().getRecipeList()) {
			if (recipe instanceof ShapedRecipeCustom) {
				CachedShapedCustomRecipe crecipe = new CachedShapedCustomRecipe((ShapedRecipeCustom) recipe);
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
		return NEIUtils.translate("handler.forestry.shaped");
	}

	@Override
	public String getGuiTexture() {
		return "minecraft:textures/gui/container/crafting_table.png";
	}

}
