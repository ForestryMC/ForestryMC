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
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.utils.ItemStackUtil;

public class ShapedRecipeCustom extends ShapedOreRecipe implements IDescriptiveRecipe {
	//Added in for future ease of change, but hard coded for now.
	private static final int MAX_CRAFT_GRID_WIDTH = 3;
	private static final int MAX_CRAFT_GRID_HEIGHT = 3;

	private ItemStack output = null;
	private Object[] input = null;
	private int width;
	private int height;
	private boolean mirrored = true;

	public ShapedRecipeCustom(ItemStack result, Object... recipe) {
		super(result, recipe);
		output = result.copy();

		String shape = "";
		int idx = 0;

		if (recipe[idx] instanceof Boolean) {
			mirrored = (Boolean) recipe[idx];
			if (recipe[idx + 1] instanceof Object[]) {
				recipe = (Object[]) recipe[idx + 1];
			} else {
				idx = 1;
			}
		}

		if (recipe[idx] instanceof String[]) {
			String[] parts = ((String[]) recipe[idx++]);

			for (String s : parts) {
				width = s.length();
				shape += s;
			}

			height = parts.length;
		} else {
			while (recipe[idx] instanceof String) {
				String s = (String) recipe[idx++];
				shape += s;
				width = s.length();
				height++;
			}
		}

		if (width * height != shape.length()) {
			String ret = "Invalid shaped ore recipe: ";
			for (Object tmp : recipe) {
				ret += tmp + ", ";
			}
			ret += output;
			throw new RuntimeException(ret);
		}

		HashMap<Character, Object> itemMap = new HashMap<Character, Object>();

		for (; idx < recipe.length; idx += 2) {
			Character chr = (Character) recipe[idx];
			Object in = recipe[idx + 1];

			if (in instanceof ItemStack) {
				itemMap.put(chr, ((ItemStack) in).copy());
			} else if (in instanceof Item) {
				itemMap.put(chr, new ItemStack((Item) in));
			} else if (in instanceof Block) {
				itemMap.put(chr, new ItemStack((Block) in, 1, OreDictionary.WILDCARD_VALUE));
			} else if (in instanceof String) {
				itemMap.put(chr, OreDictionary.getOres((String) in));
			} else {
				String ret = "Invalid shaped ore recipe: ";
				for (Object tmp : recipe) {
					ret += tmp + ", ";
				}
				ret += output;
				throw new RuntimeException(ret);
			}
		}

		input = new Object[width * height];
		int x = 0;
		for (char chr : shape.toCharArray()) {
			input[x++] = itemMap.get(chr);
		}
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

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		for (int x = 0; x <= MAX_CRAFT_GRID_WIDTH - width; x++) {
			for (int y = 0; y <= MAX_CRAFT_GRID_HEIGHT - height; ++y) {
				if (checkMatch(inv, x, y, false)) {
					return true;
				}

				if (mirrored && checkMatch(inv, x, y, true)) {
					return true;
				}
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
		for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++) {
			for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++) {
				int subX = x - startX;
				int subY = y - startY;
				Object target = null;

				if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
					if (mirror) {
						target = input[width - subX - 1 + subY * width];
					} else {
						target = input[subX + subY * width];
					}
				}

				ItemStack slot = inv.getStackInRowAndColumn(x, y);

				if (target instanceof ItemStack) {
					if (!ItemStackUtil.isCraftingEquivalent((ItemStack) target, slot)) {
						return false;
					}
				} else if (target instanceof ArrayList) {
					boolean matched = false;

					Iterator<ItemStack> itr = ((ArrayList<ItemStack>) target).iterator();
					while (itr.hasNext() && !matched) {
						matched = ItemStackUtil.isCraftingEquivalent(itr.next(), slot);
					}

					if (!matched) {
						return false;
					}
				} else if (target == null && slot != null) {
					return false;
				}
			}
		}

		return true;
	}

	public static ShapedRecipeCustom createShapedRecipe(ItemStack product, Object... materials) {
		return new ShapedRecipeCustom(product, materials);
	}
}
