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
package forestry.core.recipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.oredict.ShapedOreRecipe;

import forestry.api.recipes.IDescriptiveRecipe;

public class ShapedRecipeCustom extends ShapedOreRecipe implements IDescriptiveRecipe {
	private final int width;
	private final int height;

	public ShapedRecipeCustom(int width, int height, ItemStack product, Object... ingredients) {
		super(product, ingredients);
		this.width = width;
		this.height = height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Object[] getIngredients() {
		return getInput();
	}

	@Override
	public boolean preserveNBT() {
		return false;
	}

	@Override
	@Deprecated
	public boolean matches(IInventory inventoryCrafting, World world) {
		return false;
	}

	public static ShapedRecipeCustom createShapedRecipe(ItemStack product, Object... materials) {
		int index = 0;
		int columns = 0;
		int rows = 0;
		if (materials[index] instanceof String[]) {
			String as[] = (String[]) materials[index];
			for (String pattern : as) {
				rows++;
				columns = pattern.length();
			}
		} else {
			while (materials[index] instanceof String) {
				String pattern = (String) materials[index++];
				rows++;
				columns = pattern.length();
			}
		}

		return new ShapedRecipeCustom(columns, rows, product, materials);
	}
}
