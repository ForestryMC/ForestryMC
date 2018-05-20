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
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.utils.ItemStackUtil;

public class ShapedRecipeCustom extends ShapedOreRecipe implements IDescriptiveRecipe {
	private final NonNullList<NonNullList<ItemStack>> input;
	private final NonNullList<String> oreDicts;
	private int width;
	private int height;
	private boolean mirrored = true;

	public ShapedRecipeCustom(ItemStack result, Object... recipe) {
		super(null, result, recipe);
		ItemStack output = result.copy();

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
			String[] parts = (String[]) recipe[idx++];

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

		HashMap<Character, NonNullList<ItemStack>> itemMap = new HashMap<>();
		HashMap<Character, String> oreMap = new HashMap<>();

		for (; idx < recipe.length; idx += 2) {
			Character chr = (Character) recipe[idx];
			Object in = recipe[idx + 1];

			if (in instanceof ItemStack) {
				ItemStack copy = ((ItemStack) in).copy();
				NonNullList<ItemStack> ingredient = NonNullList.create();
				ingredient.add(copy);
				itemMap.put(chr, ingredient);
			} else if (in instanceof Item) {
				ItemStack itemStack = new ItemStack((Item) in);
				NonNullList<ItemStack> ingredient = NonNullList.create();
				ingredient.add(itemStack);
				itemMap.put(chr, ingredient);
			} else if (in instanceof Block) {
				ItemStack itemStack = new ItemStack((Block) in, 1, OreDictionary.WILDCARD_VALUE);
				NonNullList<ItemStack> ingredient = NonNullList.create();
				ingredient.add(itemStack);
				itemMap.put(chr, ingredient);
			} else if (in instanceof String) {
				itemMap.put(chr, OreDictionary.getOres((String) in));
				oreMap.put(chr, (String) in);
			} else {
				String ret = "Invalid shaped ore recipe: ";
				for (Object tmp : recipe) {
					ret += tmp + ", ";
				}
				ret += output;
				throw new RuntimeException(ret);
			}
		}

		input = NonNullList.withSize(shape.length(), NonNullList.create());
		oreDicts = NonNullList.withSize(shape.length(), "");
		int x = 0;
		for (char chr : shape.toCharArray()) {
			NonNullList<ItemStack> stacks = itemMap.get(chr);
			if (stacks != null) {
				input.set(x, stacks);
				if (oreMap.get(chr) != null) {
					oreDicts.set(x, oreMap.get(chr));
				}
			}
			x++;
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

	public NonNullList<NonNullList<ItemStack>> getRawIngredients() {
		return input;
	}

	@Override
	public NonNullList<String> getOreDicts() {
		return oreDicts;
	}

	@Override
	public ItemStack getOutput() {
		return getRecipeOutput();
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		for (int x = 0; x <= inv.getWidth() - width; x++) {
			for (int y = 0; y <= inv.getHeight() - height; ++y) {
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

	@Override
	public boolean isDynamic() {
		return false;
	}

	@Override
	public boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
		for (int x = 0; x < inv.getWidth(); x++) {
			for (int y = 0; y < inv.getHeight(); y++) {
				int subX = x - startX;
				int subY = y - startY;
				NonNullList<ItemStack> target = null;

				if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
					if (mirror) {
						target = input.get(width - subX - 1 + subY * width);
					} else {
						target = input.get(subX + subY * width);
					}
				}

				ItemStack stackInSlot = inv.getStackInRowAndColumn(x, y);

				if (target != null && !target.isEmpty()) {
					boolean matched = false;

					Iterator<ItemStack> itr = target.iterator();
					while (itr.hasNext() && !matched) {
						matched = ItemStackUtil.isCraftingEquivalent(itr.next(), stackInSlot);
					}

					if (!matched) {
						return false;
					}
				} else if (!stackInSlot.isEmpty()) {
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
