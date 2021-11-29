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
package forestry.core.utils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.core.utils.datastructures.Stack;

public abstract class ItemStackUtil {

	private static final int[] EMPTY_CONSUME = new int[0];

	/**
	 * Compares item id, damage and NBT. Accepts wildcard damage.
	 */
	public static boolean isIdenticalItem(ItemStack lhs, ItemStack rhs) {
		if (lhs == rhs) {
			return true;
		}

		if (lhs.isEmpty() || rhs.isEmpty()) {
			return false;
		}

		if (lhs.getItem() != rhs.getItem()) {
			return false;
		}

		//TODO - need tags for all of this
		//		if (lhs.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
		//			if (lhs.getItemDamage() != rhs.getItemDamage()) {
		//				return false;
		//			}
		//		}

		return ItemStack.tagMatches(lhs, rhs);
	}


	//TODO - move towards resourcelocations anyway as they are safer in some ways

	/**
	 * @return The registry name of the item as {@link String}
	 */
	@Nullable
	public static String getItemNameFromRegistryAsString(Item item) {
		ResourceLocation rl = item.getRegistryName();
		return rl == null ? null : rl.toString();
	}

	@Nullable
	public static String getStringForItemStack(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return null;
		}

		return getItemNameFromRegistryAsString(itemStack.getItem());
	}

	/**
	 * Merges the giving stack into the receiving stack as far as possible
	 */
	public static void mergeStacks(ItemStack giver, ItemStack receptor) {
		if (receptor.getCount() >= receptor.getMaxStackSize()) {
			return;
		}

		if (!receptor.sameItem(giver)) {
			return;
		}

		if (giver.getCount() <= receptor.getMaxStackSize() - receptor.getCount()) {
			receptor.grow(giver.getCount());
			giver.setCount(0);
			return;
		}

		ItemStack temp = giver.split(receptor.getMaxStackSize() - receptor.getCount());
		receptor.grow(temp.getCount());
		temp.setCount(0);
	}

	/**
	 * Creates a copy stack of the specified amount, preserving NBT data,
	 * without decreasing the source stack.
	 */

	public static ItemStack createCopyWithCount(ItemStack stack, int count) {
		ItemStack copy = stack.copy();
		copy.setCount(count);
		return copy;
	}

	public static NonNullList<ItemStack> condenseStacks(NonNullList<ItemStack> stacks) {
		NonNullList<ItemStack> condensed = NonNullList.create();

		for (ItemStack stack : stacks) {
			if (stack.isEmpty()) {
				continue;
			}

			boolean matched = false;
			for (ItemStack cached : condensed) {
				if (cached.sameItem(stack) && ItemStack.tagMatches(cached, stack)) {
					cached.grow(stack.getCount());
					matched = true;
				}
			}

			if (!matched) {
				ItemStack cached = stack.copy();
				condensed.add(cached);
			}

		}

		return condensed;
	}

	public static boolean containsItemStack(Iterable<ItemStack> list, ItemStack itemStack) {
		for (ItemStack listStack : list) {
			if (isIdenticalItem(listStack, itemStack)) {
				return true;
			}
		}
		return false;
	}

	public static int[] createConsume(NonNullList<Ingredient> set, IInventory inventory, boolean craftingTools) {
		return createConsume(set, inventory.getContainerSize(), inventory::getItem, craftingTools);
	}

	public static int[] createConsume(NonNullList<Ingredient> set, int stockCount, Function<Integer, ItemStack> stock, boolean craftingTools) {
		int totalSets = 0;

		//A array that contains the amount of items that is needed from this stack
		int[] reqAmounts = new int[stockCount];
		int found = 0;
		for (Ingredient ing : set) {
			if (ing.isEmpty()) {
				found++;
				continue;
			}
			for (ItemStack stack : ing.getItems()) {
				int curFound = found;
				for (int i = 0; i < reqAmounts.length; i++) {
					ItemStack offer = stock.apply(i);
					if (offer.getCount() > reqAmounts[i] && isCraftingEquivalent(stack, offer, craftingTools)) {
						reqAmounts[i] = reqAmounts[i] + 1;
						found++;
						break;
					}
				}
				if (found > curFound)
					break;
			}
		}
		if (found < set.size())
			return EMPTY_CONSUME;

		return reqAmounts;
	}

	public static boolean canConsume(NonNullList<Ingredient> set, IInventory inventory, boolean craftingTools) {
		return createConsume(set, inventory, craftingTools).length > 0;
	}

	public static boolean canConsume(NonNullList<Ingredient> set, IInventory inventory) {
		return canConsume(set, inventory, false);
	}

	public static boolean canConsume(NonNullList<Ingredient> set, int stockCount, Function<Integer, ItemStack> stock, boolean craftingTools) {
		return createConsume(set, stockCount, stock, craftingTools).length > 0;
	}

	/**
	 * Counts how many full sets are contained in the passed stock
	 */
	public static int containsSets(NonNullList<ItemStack> set, NonNullList<ItemStack> stock) {
		return containsSets(set, stock, false);
	}

	/**
	 * Counts how many full sets are contained in the passed stock
	 */
	public static int containsSets(NonNullList<ItemStack> set, NonNullList<ItemStack> stock, boolean craftingTools) {
		int totalSets = 0;

		NonNullList<ItemStack> condensedRequired = ItemStackUtil.condenseStacks(set);
		NonNullList<ItemStack> condensedOffered = ItemStackUtil.condenseStacks(stock);

		for (ItemStack req : condensedRequired) {

			int reqCount = 0;
			for (ItemStack offer : condensedOffered) {
				if (isCraftingEquivalent(req, offer, craftingTools)) {
					int stackCount = (int) Math.floor(offer.getCount() / req.getCount());
					reqCount = Math.max(reqCount, stackCount);
				}
			}

			if (reqCount == 0) {
				return 0;
			} else if (totalSets == 0) {
				totalSets = reqCount;
			} else if (totalSets > reqCount) {
				totalSets = reqCount;
			}
		}

		return totalSets;
	}

	/**
	 * Compare two item stacks for crafting equivalency without oreDictionary or craftingTools
	 */
	public static boolean isCraftingEquivalent(ItemStack base, ItemStack comparison) {
		if (base.isEmpty() || comparison.isEmpty()) {
			return false;
		}

		if (base.getItem() != comparison.getItem()) {
			return false;
		}

		//		if (base.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
		//			if (base.getItemDamage() != comparison.getItemDamage()) {
		//				return false;
		//			}
		//		}

		// When the base stackTagCompound is null or empty, treat it as a wildcard for crafting
		if (base.getTag() == null || base.getTag().isEmpty()) {
			return true;
		} else {
			return ItemStack.tagMatches(base, comparison);
		}
	}

	/**
	 * Compare two item stacks for crafting equivalency.
	 */
	public static boolean isCraftingEquivalent(ItemStack base, ItemStack comparison, boolean craftingTools) {
		if (base.isEmpty() || comparison.isEmpty()) {
			return false;
		}

		if (craftingTools && isCraftingToolEquivalent(base, comparison)) {
			return true;
		}

		return isCraftingEquivalent(base, comparison);

	}

	public static boolean isCraftingToolEquivalent(ItemStack base, ItemStack comparison) {
		if (base.isEmpty() || comparison.isEmpty()) {
			return false;
		}

		Item baseItem = base.getItem();

		if (baseItem != comparison.getItem()) {
			return false;
		}

		// tool uses meta for damage
		// tool uses NBT for damage
		//			if (base.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
		//				return true;
		//			}
		//base.getItemDamage() == comparison.getItemDamage();
		return base.getTag() == null || base.getTag().isEmpty();
	}

	public static void dropItemStackAsEntity(ItemStack items, World world, double x, double y, double z) {
		dropItemStackAsEntity(items, world, x, y, z, 10);
	}

	public static void dropItemStackAsEntity(ItemStack items, World world, BlockPos pos) {
		dropItemStackAsEntity(items, world, pos.getX(), pos.getY(), pos.getZ(), 10);
	}

	public static void dropItemStackAsEntity(ItemStack items, World world, double x, double y, double z, int delayForPickup) {
		if (items.isEmpty() || world.isClientSide) {
			return;
		}

		float f1 = 0.7F;
		double d = world.random.nextFloat() * f1 + (1.0F - f1) * 0.5D;
		double d1 = world.random.nextFloat() * f1 + (1.0F - f1) * 0.5D;
		double d2 = world.random.nextFloat() * f1 + (1.0F - f1) * 0.5D;
		ItemEntity entityitem = new ItemEntity(world, x + d, y + d1, z + d2, items);
		entityitem.setPickUpDelay(delayForPickup);

		world.addFreshEntity(entityitem);
	}


	public static ItemStack copyWithRandomSize(ItemStack template, int max, Random rand) {
		int size = max <= 0 ? 1 : rand.nextInt(max);
		return createCopyWithCount(template, Math.min(size, template.getMaxStackSize()));
	}

	@Nullable
	public static Block getBlock(ItemStack stack) {
		Item item = stack.getItem();

		if (item instanceof BlockItem) {
			return ((BlockItem) item).getBlock();
		} else {
			return null;
		}
	}

	public static boolean equals(Block block, ItemStack stack) {
		return block == getBlock(stack);
	}

	public static boolean equals(BlockState state, ItemStack stack) {
		return state.getBlock() == getBlock(stack);// && state.getBlock().getMetaFromState(state) == stack.getItemDamage();
	}

	public static boolean equals(Block block, int meta, ItemStack stack) {
		return block == getBlock(stack);// && meta == stack.getItemDamage();
	}


	public static List<ItemStack> parseItemStackStrings(String[] itemStackStrings, int missingMetaValue) {
		List<Stack> stacks = Stack.parseStackStrings(itemStackStrings, missingMetaValue);
		return getItemStacks(stacks);
	}


	public static List<ItemStack> parseItemStackStrings(String itemStackStrings, int missingMetaValue) {
		List<Stack> stacks = Stack.parseStackStrings(itemStackStrings, missingMetaValue);
		return getItemStacks(stacks);
	}

	@Nullable
	public static ItemStack parseItemStackString(String itemStackString, int missingMetaValue) {
		Stack stack = Stack.parseStackString(itemStackString, missingMetaValue);
		if (stack == null) {
			return null;
		}
		return getItemStack(stack);
	}

	private static List<ItemStack> getItemStacks(List<Stack> stacks) {
		List<ItemStack> itemStacks = new ArrayList<>(stacks.size());
		for (Stack stack : stacks) {
			Item item = stack.getItem();
			if (item != null) {
				int meta = stack.getMeta();
				ItemStack itemStack = new ItemStack(item, 1);//, meta);
				itemStacks.add(itemStack);
			}
		}
		return itemStacks;
	}

	@Nullable
	private static ItemStack getItemStack(Stack stack) {
		Item item = stack.getItem();
		if (item != null) {
			return new ItemStack(item, 1);//, meta);
		}
		return null;
	}

	//TODO - just use a copy and set the count to make code simpler?

	/**
	 * Checks like {@link ItemStack#areItemStacksEqual(ItemStack, ItemStack)}
	 * but ignores stack size (count).
	 */
	public static boolean areItemStacksEqualIgnoreCount(ItemStack a, ItemStack b) {
		if (a.isEmpty() && b.isEmpty()) {
			return true;
		} else if (a.isEmpty() || b.isEmpty()) {
			return false;
		} else if (a.getItem() != b.getItem()) {
			return false;
			//		} else if (a.getItemDamage() != b.getItemDamage()) {
			//			return false;
		} else if (a.getTag() == null && b.getTag() != null) {
			return false;
		} else {
			return (a.getTag() == null || a.getTag().equals(b.getTag())) && a.areCapsCompatible(b);
		}
	}
}
