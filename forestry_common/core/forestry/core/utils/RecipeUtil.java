/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.utils;

import net.minecraft.item.ItemStack;

import forestry.api.recipes.RecipeManagers;
import forestry.core.config.Defaults;
import forestry.core.interfaces.IDescriptiveRecipe;
import forestry.core.proxy.Proxies;

public class RecipeUtil {

	public static void injectLeveledRecipe(ItemStack resource, int fermentationValue, String output) {
		if (RecipeManagers.fermenterManager == null)
			return;

		RecipeManagers.fermenterManager.addRecipe(resource, fermentationValue, 1.0f, LiquidHelper.getLiquid(output, 1), LiquidHelper.getLiquid(Defaults.LIQUID_WATER, 1));

		if (LiquidHelper.exists(Defaults.LIQUID_JUICE))
			RecipeManagers.fermenterManager.addRecipe(resource, fermentationValue, 1.5f, LiquidHelper.getLiquid(output, 1), LiquidHelper.getLiquid(Defaults.LIQUID_JUICE, 1));

		if (LiquidHelper.exists(Defaults.LIQUID_HONEY))
			RecipeManagers.fermenterManager.addRecipe(resource, fermentationValue, 1.5f, LiquidHelper.getLiquid(output, 1), LiquidHelper.getLiquid(Defaults.LIQUID_HONEY, 1));
	}

	public static Object[] getCraftingRecipeAsArray(Object rec) {

		try {

			if (rec instanceof IDescriptiveRecipe) {

				IDescriptiveRecipe recipe = (IDescriptiveRecipe) rec;
				return getShapedRecipeAsArray(recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getRecipeOutput());

			}

		} catch (Exception ex) {
			Proxies.log.warning("Exception while trying to parse an ItemStack[10] from an IRecipe:");
			Proxies.log.warning(ex.getMessage());
		}

		return null;
	}

	/*
	 * private static Object[] getSmallShapedRecipeAsArray(int width, int height, Object[] ingredients, ItemStack output) { Object[] result = new Object[5];
	 * 
	 * for(int y = 0; y < height; y++) for(int x = 0; x < width; x++) result[y * 2 + x] = ingredients[y * width + x];
	 * 
	 * result[4] = output; return result; }
	 */
	private static Object[] getShapedRecipeAsArray(int width, int height, Object[] ingredients, ItemStack output) {
		Object[] result = new Object[10];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				result[y * 3 + x] = ingredients[y * width + x];
			}
		}

		result[9] = output;
		return result;
	}
}
