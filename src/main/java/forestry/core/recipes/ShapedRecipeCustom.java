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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import forestry.api.recipes.IDescriptiveRecipe;
import forestry.core.utils.ItemStackUtil;

//import net.minecraftforge.oredict.OreDictionary;
//import net.minecraftforge.oredict.ShapedOreRecipe;

//TODO - with recipes now matching on tags is this class really needed any more?
public class ShapedRecipeCustom extends ShapedRecipe implements IDescriptiveRecipe {
    private final NonNullList<NonNullList<ItemStack>> input;
    private final int width;
    private final int height;
    private boolean mirrored = true;

    public ShapedRecipeCustom(ResourceLocation id, int width, int height, NonNullList<Ingredient> input, ItemStack result) {
        super(id, null, width, height, input, result);
        ItemStack output = result.copy();

        this.width = width;
        this.height = height;
        NonNullList<Ingredient> input2 = NonNullList.withSize(input.size(), Ingredient.EMPTY);
        Collections.copy(input, input2);
        Collections.reverse(input2);
        this.mirrored = input2 == input;    //TODO - more awful code

        //Dodgy hacks for compiling here:
        List<NonNullList<ItemStack>> tempList = input.stream().map(i -> NonNullList.from(ItemStack.EMPTY, i.getMatchingStacks())).collect(Collectors.toList());
        NonNullList<ItemStack> empty = NonNullList.from(ItemStack.EMPTY);
        this.input = NonNullList.from(empty, tempList.toArray(new NonNullList[0]));
        //		StringBuilder shape = new StringBuilder();
        //		int idx = 0;
        //
        //		if (recipe[idx] instanceof Boolean) {
        //			mirrored = (Boolean) recipe[idx];
        //			if (recipe[idx + 1] instanceof Object[]) {
        //				recipe = (Object[]) recipe[idx + 1];
        //			} else {
        //				idx = 1;
        //			}
        //		}
        //
        //		if (recipe[idx] instanceof String[]) {
        //			String[] parts = (String[]) recipe[idx++];
        //
        //			for (String s : parts) {
        //				width = s.length();
        //				shape.append(s);
        //			}
        //
        //			height = parts.length;
        //		} else {
        //			while (recipe[idx] instanceof String) {
        //				String s = (String) recipe[idx++];
        //				shape.append(s);
        //				width = s.length();
        //				height++;
        //			}
        //		}
        //
        //		if (width * height != shape.length()) {
        //			StringBuilder ret = new StringBuilder("Invalid shaped ore recipe: ");
        //			for (Object tmp : recipe) {
        //				ret.append(tmp).append(", ");
        //			}
        //			ret.append(output);
        //			throw new RuntimeException(ret.toString());
        //		}
        //
        //		HashMap<Character, NonNullList<ItemStack>> itemMap = new HashMap<>();
        //		HashMap<Character, String> oreMap = new HashMap<>();
        //
        //		for (; idx < recipe.length; idx += 2) {
        //			Character chr = (Character) recipe[idx];
        //			Object in = recipe[idx + 1];
        //
        //			if (in instanceof ItemStack) {
        //				ItemStack copy = ((ItemStack) in).copy();
        //				NonNullList<ItemStack> ingredient = NonNullList.create();
        //				ingredient.add(copy);
        //				itemMap.put(chr, ingredient);
        //			} else if (in instanceof Item) {
        //				ItemStack itemStack = new ItemStack((Item) in);
        //				NonNullList<ItemStack> ingredient = NonNullList.create();
        //				ingredient.add(itemStack);
        //				itemMap.put(chr, ingredient);
        //			} else if (in instanceof Block) {
        //				ItemStack itemStack = new ItemStack((Block) in, 1, OreDictionary.WILDCARD_VALUE);
        //				NonNullList<ItemStack> ingredient = NonNullList.create();
        //				ingredient.add(itemStack);
        //				itemMap.put(chr, ingredient);
        //			} else if (in instanceof String) {
        //				itemMap.put(chr, OreDictionary.getOres((String) in));
        //				oreMap.put(chr, (String) in);
        //			} else {
        //				StringBuilder ret = new StringBuilder("Invalid shaped ore recipe: ");
        //				for (Object tmp : recipe) {
        //					ret.append(tmp).append(", ");
        //				}
        //				ret.append(output);
        //				throw new RuntimeException(ret.toString());
        //			}
        //		}
        //
        //		input = NonNullList.withSize(shape.length(), NonNullList.create());
        //		oreDicts = NonNullList.withSize(shape.length(), "");
        //		int x = 0;
        //		for (char chr : shape.toString().toCharArray()) {
        //			NonNullList<ItemStack> stacks = itemMap.get(chr);
        //			if (stacks != null) {
        //				input.set(x, stacks);
        //				if (oreMap.get(chr) != null) {
        //					oreDicts.set(x, oreMap.get(chr));
        //				}
        //			}
        //			x++;
        //		}
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
        return NonNullList.create();
    }

    @Override
    public ItemStack getOutput() {
        return getRecipeOutput();
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
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

    //TODO - either AT or implement separately
    //	@Override
    private boolean checkMatch(CraftingInventory inv, int startX, int startY, boolean mirror) {
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

                //TODO
                ItemStack stackInSlot = ItemStack.EMPTY;//inv.getStackInRowAndColumn(x, y);

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

    public static ShapedRecipeCustom createShapedRecipe(ResourceLocation id, int width, int height, NonNullList<Ingredient> input, ItemStack result) {
        return new ShapedRecipeCustom(id, width, height, input, result);
    }
}
