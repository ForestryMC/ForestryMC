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

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;

public class ShapedRecipeCustom implements IDescriptiveRecipe {

	private final int width;
	private final int height;

	private final Object[] ingredients;
	private final ItemStack product;

	public ShapedRecipeCustom(int width, int height, Object[] ingredients, ItemStack product) {
		this.width = width;
		this.height = height;
		this.ingredients = ingredients;
		this.product = product;
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
	public ItemStack getRecipeOutput() {
		return product.copy();
	}

	@Override
	public Object[] getIngredients() {
		return ingredients;
	}

	@Override
	public boolean preserveNBT() {
		return false;
	}

	@Override
	public boolean matches(InventoryCrafting inventoryCrafting, World world) {
		return RecipeUtil.matches(this, inventoryCrafting);
	}

	@Override
	@Deprecated
	public boolean matches(IInventory inventoryCrafting, World world) {
		return RecipeUtil.matches(this, inventoryCrafting);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
		return getRecipeOutput().copy();
	}

	@Override
	public int getRecipeSize() {
		return width * height;
	}

	public static ShapedRecipeCustom createShapedRecipe(ItemStack product, Object... materials) {

		String s = "";
		int index = 0;
		int columns = 0;
		int rows = 0;
		if (materials[index] instanceof String[]) {
			String as[] = (String[]) materials[index++];
			for (String pattern : as) {
				rows++;
				columns = pattern.length();
				s = (new StringBuilder()).append(s).append(pattern).toString();
			}

		} else {
			while (materials[index] instanceof String) {
				String pattern = (String) materials[index++];
				rows++;
				columns = pattern.length();
				s = (new StringBuilder()).append(s).append(pattern).toString();
			}
		}

		HashMap<Character, Object> hashmap = new HashMap<>();
		for (; index < materials.length; index += 2) {

			Character character = (Character) materials[index];

			// Item
			if (materials[index + 1] instanceof Item) {
				hashmap.put(character, new ItemStack((Item) materials[index + 1]));
			} else if (materials[index + 1] instanceof ForestryItem) {
				hashmap.put(character, ((ForestryItem) materials[index + 1]).getItemStack());
			} else if (materials[index + 1] instanceof ForestryBlock) {
				hashmap.put(character, ((ForestryBlock) materials[index + 1]).getItemStack());
			} else if (materials[index + 1] instanceof Block) {
				hashmap.put(character, new ItemStack((Block) materials[index + 1], 1, OreDictionary.WILDCARD_VALUE));
			} else if (materials[index + 1] instanceof ItemStack) {
				hashmap.put(character, materials[index + 1]);
			} else if (materials[index + 1] instanceof String) {
				hashmap.put(character, OreDictionary.getOres((String) materials[index + 1]));
			} else {
				throw new RuntimeException("Invalid Recipe Defined!");
			}

		}

		Object ingredients[] = new Object[columns * rows];
		for (int l = 0; l < columns * rows; l++) {
			char c = s.charAt(l);
			if (hashmap.containsKey(c)) {
				ingredients[l] = hashmap.get(c);
			} else {
				ingredients[l] = null;
			}
		}

		return new ShapedRecipeCustom(columns, rows, ingredients, product);
	}

	@SuppressWarnings("unchecked")
	public static ShapedRecipeCustom buildRecipe(ItemStack product, Object... materials) {
		ShapedRecipeCustom recipe = createShapedRecipe(product, materials);
		CraftingManager.getInstance().getRecipeList().add(recipe);
		return recipe;
	}

	@SuppressWarnings("unchecked")
	public static ShapedRecipeCustom buildPriorityRecipe(ItemStack product, Object... materials) {
		ShapedRecipeCustom recipe = createShapedRecipe(product, materials);
		CraftingManager.getInstance().getRecipeList().add(0, recipe);
		return recipe;
	}
}
