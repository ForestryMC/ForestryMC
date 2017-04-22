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

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.oredict.OreDictionary;

public abstract class ItemStackUtil {

	public static final ItemStack[] EMPTY_STACK_ARRAY = new ItemStack[0];

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

		if (lhs.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
			if (lhs.getItemDamage() != rhs.getItemDamage()) {
				return false;
			}
		}

		return ItemStack.areItemStackTagsEqual(lhs, rhs);
	}

	/**
	 * @return The registry name of the item as {@link ResourceLocation}
	 */
	@Nullable
	public static ResourceLocation getItemNameFromRegistry(Item item) {
		IForgeRegistry<Item> itemRegistry = ForgeRegistries.ITEMS;
		if (itemRegistry.containsValue(item)) {
			return itemRegistry.getKey(item);
		} else {
			return null;
		}
	}
	
	/**
	 * @return The registry name of the block as {@link ResourceLocation}
	 */
	@Nullable
	public static String getBlockNameFromRegistryAsString(Block block) {
		IForgeRegistry<Block> itemRegistry = ForgeRegistries.BLOCKS;
		if (itemRegistry.containsValue(block)) {
			return itemRegistry.getKey(block).toString();
		} else {
			return null;
		}
	}
	
	/**
	 * @return The registry name of the block as {@link ResourceLocation}
	 */
	@Nullable
	public static ResourceLocation getBlockNameFromRegistry(Block block) {
		IForgeRegistry<Block> itemRegistry = ForgeRegistries.BLOCKS;
		if (itemRegistry.containsValue(block)) {
			return itemRegistry.getKey(block);
		} else {
			return null;
		}
	}

	/**
	 * @return The registry name of the item as {@link String}
	 */
	@Nullable
	public static String getItemNameFromRegistryAsString(Item item) {
		ResourceLocation itemNameFromRegistry = getItemNameFromRegistry(item);
		if (itemNameFromRegistry == null) {
			return null;
		}
		return itemNameFromRegistry.toString();
	}

	@Nullable
	public static String getStringForItemStack(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return null;
		}

		Item item = itemStack.getItem();
		String itemStackString = getItemNameFromRegistryAsString(item);
		if (itemStackString == null) {
			return null;
		}

		int meta = itemStack.getItemDamage();
		if (meta != OreDictionary.WILDCARD_VALUE) {
			return itemStackString + ':' + meta;
		} else {
			return itemStackString;
		}
	}

	/**
	 * @param itemName The registry name from the item as {@link String}
	 * @return The item from the item registry
	 */
	@Nullable
	public static Item getItemFromRegistry(String itemName) {
		return getItemFromRegistry(new ResourceLocation(itemName));
	}

	/**
	 * @param itemName The registry name from the item as {@link ResourceLocation}
	 * @return The item from the item registry
	 */
	@Nullable
	public static Item getItemFromRegistry(ResourceLocation itemName) {
		IForgeRegistry<Item> itemRegistry = ForgeRegistries.ITEMS;
		if (!itemRegistry.containsKey(itemName)) {
			return null;
		}
		return itemRegistry.getValue(itemName);
	}

	/**
	 * @param itemName The registry name from the block as {@link String}
	 * @return The block from the block registry
	 */
	@Nullable
	public static Block getBlockFromRegistry(String itemName) {
		return getBlockFromRegistry(new ResourceLocation(itemName));
	}

	/**
	 * @param itemName The registry name from the block as {@link ResourceLocation}
	 * @return The block from the block registry
	 */
	@Nullable
	public static Block getBlockFromRegistry(ResourceLocation itemName) {
		IForgeRegistry<Block> blockRegistry = ForgeRegistries.BLOCKS;
		if (!blockRegistry.containsKey(itemName)) {
			return null;
		}
		return blockRegistry.getValue(itemName);
	}

	/**
	 * Merges the giving stack into the receiving stack as far as possible
	 */
	public static void mergeStacks(ItemStack giver, ItemStack receptor) {
		if (receptor.getCount() >= receptor.getMaxStackSize()) {
			return;
		}

		if (!receptor.isItemEqual(giver)) {
			return;
		}

		if (giver.getCount() <= receptor.getMaxStackSize() - receptor.getCount()) {
			receptor.grow(giver.getCount());
			giver.setCount(0);
			return;
		}

		ItemStack temp = giver.splitStack(receptor.getMaxStackSize() - receptor.getCount());
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
				if (cached.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(cached, stack)) {
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
	
	public static Pair<NonNullList<ItemStack>, NonNullList<String>> condenseStacks(NonNullList<ItemStack> stacks, NonNullList<String> dicts) {
		NonNullList<ItemStack> condensed = NonNullList.create();
		NonNullList<String> condensedDicts = NonNullList.create();

		for (int i = 0;i < stacks.size();i++) {
			ItemStack stack = stacks.get(i);
			if (stack.isEmpty()) {
				continue;
			}

			boolean matched = false;
			for (ItemStack cached : condensed) {
				if (cached.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(cached, stack)) {
					cached.grow(stack.getCount());
					matched = true;
				}
			}

			if (!matched) {
				ItemStack cached = stack.copy();
				condensed.add(cached);
				condensedDicts.add(dicts.get(i));
			}

		}

		return Pair.of(condensed, condensedDicts);
	}

	public static boolean containsItemStack(Iterable<ItemStack> list, ItemStack itemStack) {
		for (ItemStack listStack : list) {
			if (isIdenticalItem(listStack, itemStack)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Counts how many full sets are contained in the passed stock
	 */
	public static int containsSets(NonNullList<ItemStack> set, NonNullList<ItemStack> stock) {
		return containsSets(set, stock, false, false);
	}

	/**
	 * Counts how many full sets are contained in the passed stock
	 */
	public static int containsSets(NonNullList<ItemStack> set, NonNullList<ItemStack> stock, boolean oreDictionary, boolean craftingTools) {
		int totalSets = 0;

		NonNullList<ItemStack> condensedRequired = ItemStackUtil.condenseStacks(set);
		NonNullList<ItemStack> condensedOffered = ItemStackUtil.condenseStacks(stock);

		for (ItemStack req : condensedRequired) {

			int reqCount = 0;
			for (ItemStack offer : condensedOffered) {
				if (isCraftingEquivalent(req, offer, oreDictionary, craftingTools)) {
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
	 * Counts how many full sets are contained in the passed stock
	 */
	public static int containsSets(NonNullList<ItemStack> set, NonNullList<ItemStack> stock, NonNullList<String> oreDicts, boolean craftingTools) {
		int totalSets = 0;

		Pair<NonNullList<ItemStack>, NonNullList<String>> condensedRequired = ItemStackUtil.condenseStacks(set, oreDicts);
		NonNullList<String> condensedRequiredDicts= condensedRequired.getRight();
		NonNullList<ItemStack> condensedRequiredStacks = condensedRequired.getLeft();
		NonNullList<ItemStack> condensedOfferedStacks = ItemStackUtil.condenseStacks(stock);

		for (int y = 0;y < condensedRequiredStacks.size();y++) {
			ItemStack req = condensedRequiredStacks.get(y);
			String offerDict = condensedRequiredDicts.get(y);
			int reqCount = 0;
			for (int i = 0;i < condensedOfferedStacks.size();i++) {
				ItemStack offer = condensedOfferedStacks.get(i);
				if (isCraftingEquivalent(req, offer, offerDict, craftingTools)) {
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

	public static boolean equalSets(NonNullList<ItemStack> set1, NonNullList<ItemStack> set2) {
		if (set1 == set2) {
			return true;
		}

		if (set1.size() != set2.size()) {
			return false;
		}

		for (int i = 0; i < set1.size(); i++) {
			if (!isIdenticalItem(set1.get(i), set2.get(i))) {
				return false;
			}
		}

		return true;
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

		if (base.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
			if (base.getItemDamage() != comparison.getItemDamage()) {
				return false;
			}
		}

		// When the base stackTagCompound is null or empty, treat it as a wildcard for crafting
		if (base.getTagCompound() == null || base.getTagCompound().hasNoTags()) {
			return true;
		} else {
			return ItemStack.areItemStackTagsEqual(base, comparison);
		}
	}
	
	/**
	 * Compare two item stacks for crafting equivalency.
	 */
	public static boolean isCraftingEquivalent(ItemStack base, ItemStack comparison, boolean oreDictionary, boolean craftingTools) {
		if (isCraftingEquivalent(base, comparison, craftingTools)) {
			return true;
		}

		if (oreDictionary) {
			int[] idsBase = OreDictionary.getOreIDs(base);
			Arrays.sort(idsBase);
			int[] idsComp = OreDictionary.getOreIDs(comparison);
			Arrays.sort(idsComp);

			// check if the sorted arrays "idsBase" and "idsComp" have any ID in common.
			int iBase = 0;
			int iComp = 0;
			while (iBase < idsBase.length && iComp < idsComp.length) {
				if (idsBase[iBase] < idsComp[iComp]) {
					iBase++;
				} else if (idsBase[iBase] > idsComp[iComp]) {
					iComp++;
				} else {
					return true;
				}
			}
			return false;
		}

		return false;
	}
	
	/**
	 * Compare two item stacks for crafting equivalency.
	 */
	public static boolean isCraftingEquivalent(ItemStack base, ItemStack comparison, @Nullable String oreDict, boolean craftingTools) {
		if(isCraftingEquivalent(base, comparison, craftingTools)){
			return true;
		}

		if (oreDict != null && !oreDict.isEmpty()) {
			int[] validIds = OreDictionary.getOreIDs(comparison);
			int validID = OreDictionary.getOreID(oreDict);

			for(int id : validIds){
				if (id == validID) {
					return true;
				}
			}
		}

		return false;
	}
	
	public static boolean isCraftingEquivalent(ItemStack base, ItemStack comparison, boolean craftingTools) {
		if (base.isEmpty() || comparison.isEmpty()) {
			return false;
		}

		if (craftingTools && isCraftingToolEquivalent(base, comparison)) {
			return true;
		}

		if (isCraftingEquivalent(base, comparison)) {
			return true;
		}

		if (base.getTagCompound() != null && !base.getTagCompound().hasNoTags()) {
			if (!ItemStack.areItemStacksEqual(base, comparison)) {
				return false;
			}
		}

		return false;
	}

	public static boolean isCraftingToolEquivalent(ItemStack base, ItemStack comparison) {
		if (base.isEmpty() || comparison.isEmpty()) {
			return false;
		}

		Item baseItem = base.getItem();

		if (baseItem != comparison.getItem()) {
			return false;
		}

		if (base.getTagCompound() == null || base.getTagCompound().hasNoTags()) {
			// tool uses meta for damage
			return true;
		} else {
			// tool uses NBT for damage
			if (base.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
				return true;
			}
			return base.getItemDamage() == comparison.getItemDamage();
		}
	}

	public static void dropItemStackAsEntity(ItemStack items, World world, double x, double y, double z) {
		dropItemStackAsEntity(items, world, x, y, z, 10);
	}

	public static void dropItemStackAsEntity(ItemStack items, World world, BlockPos pos) {
		dropItemStackAsEntity(items, world, pos.getX(), pos.getY(), pos.getZ(), 10);
	}

	public static void dropItemStackAsEntity(ItemStack items, World world, double x, double y, double z, int delayForPickup) {
		if (items.isEmpty() || world.isRemote) {
			return;
		}

		float f1 = 0.7F;
		double d = world.rand.nextFloat() * f1 + (1.0F - f1) * 0.5D;
		double d1 = world.rand.nextFloat() * f1 + (1.0F - f1) * 0.5D;
		double d2 = world.rand.nextFloat() * f1 + (1.0F - f1) * 0.5D;
		EntityItem entityitem = new EntityItem(world, x + d, y + d1, z + d2, items);
		entityitem.setPickupDelay(delayForPickup);

		world.spawnEntity(entityitem);
	}


	public static ItemStack copyWithRandomSize(ItemStack template, int max, Random rand) {
		int size = max <= 0 ? 0 : rand.nextInt(max);
		ItemStack created = template.copy();
		if (size <= 0) {
			created.setCount(1);
		} else if (size > created.getMaxStackSize()) {
			created.setCount(created.getMaxStackSize());
		} else {
			created.setCount(size);
		}
		return created;
	}

	@Nullable
	public static Block getBlock(ItemStack stack) {
		Item item = stack.getItem();

		if (item instanceof ItemBlock) {
			return ((ItemBlock) item).getBlock();
		} else {
			return null;
		}
	}

	public static boolean equals(Block block, ItemStack stack) {
		return block == getBlock(stack);
	}

	public static boolean equals(IBlockState state, ItemStack stack) {
		return state.getBlock() == getBlock(stack) && state.getBlock().getMetaFromState(state) == stack.getItemDamage();
	}

	public static boolean equals(Block block, int meta, ItemStack stack) {
		return block == getBlock(stack) && meta == stack.getItemDamage();
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
				ItemStack itemStack = new ItemStack(item, 1, meta);
				itemStacks.add(itemStack);
			}
		}
		return itemStacks;
	}

	@Nullable
	private static ItemStack getItemStack(Stack stack) {
		Item item = stack.getItem();
		if (item != null) {
			int meta = stack.getMeta();
			return new ItemStack(item, 1, meta);
		}
		return null;
	}

}
