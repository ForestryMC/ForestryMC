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

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;

public class ShapedRecipeCustom implements IDescriptiveRecipe {

	private final int width;
	private final int height;

	private final Object[] ingredients;
	private final ItemStack product;

	private boolean preserveNBT = false;

	public ShapedRecipeCustom(int width, int height, Object[] ingredients, ItemStack product) {
		this.width = width;
		this.height = height;
		this.ingredients = ingredients;
		this.product = product;
	}

	public ShapedRecipeCustom setPreserveNBT() {
		this.preserveNBT = true;
		return this;
	}

	public boolean preservesNbt() {
		return preserveNBT;
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
	public boolean matches(InventoryCrafting inventoryCrafting, World world) {
		if (!matches((IInventory) inventoryCrafting, world)) {
			return false;
		}

		if (preserveNBT) {
			if (RecipeUtil.getCraftingNbt(inventoryCrafting) == null) {
				return false;
			}
		}

		return true;
	}

	public boolean matches(IInventory inventoryCrafting, World world) {
		ItemStack[][] resources = new ItemStack[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int k = i + j * 3;
				resources[i][j] = inventoryCrafting.getStackInSlot(k);
			}
		}

		return matches(resources);
	}

	public boolean matches(ItemStack[][] resources) {

		for (int i = 0; i <= 3 - width; i++) {
			for (int j = 0; j <= 3 - height; j++) {
				if (checkMatch(resources, i, j, true)) {
					return true;
				}

				if (checkMatch(resources, i, j, false)) {
					return true;
				}
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean checkMatch(ItemStack[][] resources, int xInGrid, int yInGrid, boolean flag) {

		for (int k = 0; k < 3; k++) {
			for (int l = 0; l < 3; l++) {

				int widthIt = k - xInGrid;
				int heightIt = l - yInGrid;
				Object compare = null;

				if (widthIt >= 0 && heightIt >= 0 && widthIt < width && heightIt < height) {
					if (flag) {
						compare = ingredients[(width - widthIt - 1) + heightIt * width];
					} else {
						compare = ingredients[widthIt + heightIt * width];
					}
				}
				ItemStack resource = resources[k][l];

				if (compare instanceof ItemStack) {
					if (!checkItemMatch((ItemStack) compare, resource)) {
						return false;
					}
				} else if (compare instanceof ArrayList) {
					boolean matched = false;

					for (ItemStack item : (ArrayList<ItemStack>) compare) {
						matched = matched || checkItemMatch(item, resource);
					}

					if (!matched) {
						return false;
					}

				} else if (compare == null && resource != null) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean checkItemMatch(ItemStack compare, ItemStack resource) {

		if (resource == null && compare == null) {
			return true;
		}

		if (resource == null || compare == null) {
			return false;
		}

		if (compare.getItem() != resource.getItem()) {
			return false;
		}

		if (compare.getItemDamage() != OreDictionary.WILDCARD_VALUE && compare.getItemDamage() != resource.getItemDamage()) {
			return false;
		}

		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
		return getCraftingResult((IInventory) inventoryCrafting);
	}

	public ItemStack getCraftingResult(IInventory inventoryCrafting) {
		ItemStack result = product.copy();

		if (preserveNBT) {
			NBTTagCompound craftingNbt = RecipeUtil.getCraftingNbt(inventoryCrafting);
			if (craftingNbt == null) {
				return null;
			}

			result.setTagCompound(craftingNbt);
		}

		return result;
	}

	@Override
	public int getRecipeSize() {
		return width * height;
	}

	/**
	 * @return true if resource is a valid ingredient in this recipe.
	 */
	@SuppressWarnings("unchecked")
	public boolean isIngredient(ItemStack resource) {

		for (Object ingredient : ingredients) {
			if (ingredient instanceof ItemStack) {
				if (checkItemMatch((ItemStack) ingredient, resource)) {
					return true;
				}

			} else if (ingredient instanceof ArrayList) {
				for (ItemStack item : (ArrayList<ItemStack>) ingredient) {
					if (checkItemMatch(item, resource)) {
						return true;
					}
				}
			}
		}

		return false;

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
