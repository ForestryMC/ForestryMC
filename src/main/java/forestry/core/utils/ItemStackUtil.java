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

import java.util.Random;
import java.util.function.Function;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ItemStackUtil {

	private static final int[] EMPTY_CONSUME = new int[0];

	/**
	 * Compares item id, damage and NBT. Accepts wildcard damage.
	 */
	public static boolean isIdenticalItem(ItemStack lhs, ItemStack rhs) {
		return lhs.equals(rhs, false);
	}

	/**
	 * Merges the giving stack into the receiving stack as far as possible
	 */
	public static void mergeStacks(ItemStack giver, ItemStack receptor) {
		int maxInsert = receptor.getMaxStackSize() - receptor.getCount();
		int maxExtract = giver.getCount();
		int canTransfer = Math.min(maxInsert, maxExtract);

		giver.shrink(canTransfer);
		receptor.grow(canTransfer);
	}

	public static NonNullList<ItemStack> condenseStacks(NonNullList<ItemStack> stacks) {
		Object2IntMap<ItemStack> map = new Object2IntOpenHashMap<>();

		for (ItemStack stack : stacks) {
			ItemStack copy = stack.copy();
			copy.setCount(1);

			map.put(copy, map.getInt(copy) + stack.getCount());
		}

		NonNullList<ItemStack> condensed = NonNullList.create();

		for (Object2IntMap.Entry<ItemStack> entry : map.object2IntEntrySet()) {
			ItemStack stack = entry.getKey();
			int count = entry.getIntValue();

			while (count > 0) {
				int transfer = Math.min(count, stack.getMaxStackSize());
				count -= transfer;

				ItemStack copy = stack.copy();
				copy.setCount(transfer);
				condensed.add(copy);
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
				if (found > curFound) {
					break;
				}
			}
		}
		if (found < set.size()) {
			return EMPTY_CONSUME;
		}

		return reqAmounts;
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
					int stackCount = offer.getCount() / req.getCount();
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

		// tool uses NBT for damage
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
		ItemStack copy = template.copy();
		copy.setCount(Math.min(size, template.getMaxStackSize()));
		return copy;
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

	/**
	 * Checks if two items are exactly the same, ignoring counts
	 */
	public static boolean areItemStacksEqualIgnoreCount(ItemStack a, ItemStack b) {
		int countA = a.getCount();
		int countB = b.getCount();
		boolean equals = a.equals(b, false);
		a.setCount(countA);
		b.setCount(countB);
		return equals;
	}
}
