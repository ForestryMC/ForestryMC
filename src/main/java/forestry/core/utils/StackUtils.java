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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.oredict.OreDictionary;

import forestry.core.config.Defaults;
import forestry.core.inventory.InvTools;
import forestry.core.proxy.Proxies;

public class StackUtils {

	public static final ItemStack[] EMPTY_STACK_ARRAY = new ItemStack[0];

	/**
	 * Compares item id, damage and NBT. Accepts wildcard damage.
	 */
	public static boolean isIdenticalItem(ItemStack lhs, ItemStack rhs) {
		if (lhs == null || rhs == null) {
			return false;
		}

		if (lhs.getItem() != rhs.getItem()) {
			return false;
		}

		if (lhs.getItemDamage() != Defaults.WILDCARD) {
			if (lhs.getItemDamage() != rhs.getItemDamage()) {
				return false;
			}
		}

		return ItemStack.areItemStackTagsEqual(lhs, rhs);
	}

	public static boolean stowInInventory(ItemStack itemstack, IInventory inventory, boolean doAdd) {
		return stowInInventory(itemstack, inventory, doAdd, 0, inventory.getSizeInventory());
	}

	public static boolean stowInInventory(ItemStack itemstack, IInventory inventory, boolean doAdd, int slot1,
			int count) {

		boolean added = false;

		for (int i = slot1; i < slot1 + count; i++) {
			ItemStack inventoryStack = inventory.getStackInSlot(i);

			// Grab those free slots
			if (inventoryStack == null) {
				if (doAdd) {
					inventory.setInventorySlotContents(i, itemstack.copy());
					itemstack.stackSize = 0;
				}
				return true;
			}

			// Already full
			if (inventoryStack.stackSize >= inventoryStack.getMaxStackSize()) {
				continue;
			}

			// Not same type
			if (!inventoryStack.isItemEqual(itemstack)) {
				continue;
			}
			if (!ItemStack.areItemStackTagsEqual(inventoryStack, itemstack)) {
				continue;
			}

			int space = inventoryStack.getMaxStackSize() - inventoryStack.stackSize;

			// Enough space to add all
			if (space > itemstack.stackSize) {
				if (doAdd) {
					inventoryStack.stackSize += itemstack.stackSize;
					itemstack.stackSize = 0;
				}
				return true;
				// Only part can be added
			} else {
				if (doAdd) {
					inventoryStack.stackSize = inventoryStack.getMaxStackSize();
					itemstack.stackSize -= space;
				}
				added = true;
			}

		}

		return added;
	}

	/**
	 * Merges the giving stack into the receiving stack as far as possible
	 */
	public static void mergeStacks(ItemStack giver, ItemStack receptor) {
		if (receptor.stackSize >= 64) {
			return;
		}

		if (!receptor.isItemEqual(giver)) {
			return;
		}

		if (giver.stackSize <= (receptor.getMaxStackSize() - receptor.stackSize)) {
			receptor.stackSize += giver.stackSize;
			giver.stackSize = 0;
			return;
		}

		ItemStack temp = giver.splitStack(receptor.getMaxStackSize() - receptor.stackSize);
		receptor.stackSize += temp.stackSize;
		temp.stackSize = 0;
	}

	/**
	 * Creates a split stack of the specified amount, preserving NBT data,
	 * without decreasing the source stack.
	 */
	public static ItemStack createSplitStack(ItemStack stack, int amount) {
		ItemStack split = new ItemStack(stack.getItem(), amount, stack.getItemDamage());
		if (stack.getTagCompound() != null) {
			NBTTagCompound nbttagcompound = (NBTTagCompound) stack.getTagCompound().copy();
			split.setTagCompound(nbttagcompound);
		}
		return split;
	}

	/**
	 */
	public static ItemStack[] condenseStacks(ItemStack[] stacks) {
		List<ItemStack> condensed = new ArrayList<ItemStack>();

		for (ItemStack stack : stacks) {
			if (stack == null) {
				continue;
			}
			if (stack.stackSize <= 0) {
				continue;
			}

			boolean matched = false;
			for (ItemStack cached : condensed) {
				if ((cached.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(cached, stack))) {
					cached.stackSize += stack.stackSize;
					matched = true;
				}
			}

			if (!matched) {
				ItemStack cached = stack.copy();
				condensed.add(cached);
			}

		}

		return condensed.toArray(new ItemStack[condensed.size()]);
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
	public static int containsSets(ItemStack[] set, ItemStack[] stock) {
		return containsSets(set, stock, false, false);
	}

	/**
	 * Counts how many full sets are contained in the passed stock
	 */
	public static int containsSets(ItemStack[] set, ItemStack[] stock, boolean oreDictionary, boolean craftingTools) {
		int totalSets = 0;

		ItemStack[] condensedRequired = StackUtils.condenseStacks(set);
		ItemStack[] condensedOffered = StackUtils.condenseStacks(stock);

		for (ItemStack req : condensedRequired) {

			int reqCount = 0;
			for (ItemStack offer : condensedOffered) {
				if (isCraftingEquivalent(req, offer, oreDictionary, craftingTools)) {
					int stackCount = (int) Math.floor(offer.stackSize / req.stackSize);
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
	 * Compare two item stacks for crafting equivalency without oreDictionary or
	 * craftingTools
	 */
	public static boolean isCraftingEquivalent(ItemStack base, ItemStack comparison) {
		if (base == null || comparison == null) {
			return false;
		}

		if (base.getItem() != comparison.getItem()) {
			return false;
		}

		if (base.getItemDamage() != Defaults.WILDCARD) {
			if (base.getItemDamage() != comparison.getItemDamage()) {
				return false;
			}
		}

		// When the base stackTagCompound is null or empty, treat it as a
		// wildcard for crafting
		if (base.getTagCompound() == null || base.getTagCompound().hasNoTags()) {
			return true;
		} else {
			return ItemStack.areItemStackTagsEqual(base, comparison);
		}
	}

	/**
	 * Compare two item stacks for crafting equivalency.
	 */
	public static boolean isCraftingEquivalent(ItemStack base, ItemStack comparison, boolean oreDictionary,
			boolean craftingTools) {
		if (isCraftingEquivalent(base, comparison)) {
			return true;
		}

		if (base == null || comparison == null) {
			return false;
		}

		if (base.hasTagCompound() && !base.getTagCompound().hasNoTags()) {
			if (!ItemStack.areItemStacksEqual(base, comparison)) {
				return false;
			}
		}

		if (oreDictionary) {
			int[] idsBase = OreDictionary.getOreIDs(base);
			Arrays.sort(idsBase);
			int[] idsComp = OreDictionary.getOreIDs(comparison);
			Arrays.sort(idsComp);

			// check if the sorted arrays "idsBase" and "idsComp" have any ID in
			// common.
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
		}

		if (craftingTools) {
			return isThisCraftingTool(base, comparison);
		}

		return false;
	}

	public static boolean isCraftingTool(ItemStack itemstack) {
		return itemstack.getItem().hasContainerItem(itemstack) && itemstack.getItem().isDamageable();
	}

	public static boolean isThisCraftingTool(ItemStack phantom, ItemStack actual) {
		return isCraftingTool(phantom) && phantom.getItem() == actual.getItem();
	}

	public static void stowContainerItem(ItemStack itemstack, IInventory stowing, int slotIndex, EntityPlayer player) {
		if (!itemstack.getItem().hasContainerItem(itemstack)) {
			return;
		}

		ItemStack container = itemstack.getItem().getContainerItem(itemstack);

		if (container.isItemStackDamageable() && container.getItemDamage() > container.getMaxDamage()) {
			if (player != null) {
				MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, container));
			}
			container = null;
		}

		if (container != null) {
			if (!InvTools.tryAddStack(stowing, container, slotIndex, 1, true)) {
				if (!InvTools.tryAddStack(stowing, container, true) && player != null) {
					player.dropPlayerItemWithRandomChoice(container, true);
				}
			}
		}
	}

	public static void dropItemStackAsEntity(ItemStack items, World world, double x, double y, double z) {
		if (items.stackSize <= 0) {
			return;
		}

		float f1 = 0.7F;
		double d = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
		double d1 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
		double d2 = (world.rand.nextFloat() * f1) + (1.0F - f1) * 0.5D;
		EntityItem entityitem = new EntityItem(world, x + d, y + d1, z + d2, items);
		entityitem.setPickupDelay(10);

		world.spawnEntityInWorld(entityitem);

	}

	public static ItemStack copyWithRandomSize(ItemStack template, int max, Random rand) {
		int size = rand.nextInt(max);
		ItemStack created = template.copy();
		created.stackSize = size <= 0 ? 1 : size > created.getMaxStackSize() ? created.getMaxStackSize() : size;
		return created;
	}

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

	public static boolean equals(Block block, int meta, ItemStack stack) {
		return block == getBlock(stack) && meta == stack.getItemDamage();
	}

	public static class Stack {
		private final String name;
		private final int meta;

		public Stack(String name, int meta) {
			this.name = name;
			this.meta = meta;
		}

		public Item getItem() {
			Item item = GameData.getItemRegistry().getRaw(name);
			if (item == null) {
				Proxies.log.warning("Failed to find item for (" + name + ") in the Forge registry.");
			}
			return item;
		}

		public Block getBlock() {
			Block block = GameData.getBlockRegistry().getRaw(name);
			if (block == null) {
				Proxies.log.warning("Failed to find block for (" + name + ") in the Forge registry.");
			}
			return block;
		}

		public int getMeta() {
			return meta;
		}
	}

	public static List<ItemStack> parseItemStackStrings(String[] itemStackStrings, int missingMetaValue) {
		List<Stack> stacks = parseStackStrings(itemStackStrings, missingMetaValue);
		return getItemStacks(stacks);
	}

	public static List<ItemStack> parseItemStackStrings(String itemStackStrings, int missingMetaValue) {
		List<Stack> stacks = parseStackStrings(itemStackStrings, missingMetaValue);
		return getItemStacks(stacks);
	}

	private static List<ItemStack> getItemStacks(List<Stack> stacks) {
		List<ItemStack> itemStacks = new ArrayList<ItemStack>(stacks.size());
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

	public static List<Stack> parseStackStrings(String itemStackStrings, int missingMetaValue) {
		String[] parts = itemStackStrings.split("(\\s*;\\s*)+");
		return parseStackStrings(parts, missingMetaValue);
	}

	public static List<Stack> parseStackStrings(String[] parts, int missingMetaValue) {

		List<Stack> stacks = new ArrayList<Stack>();

		for (String itemStackString : parts) {
			Stack stack = parseStackString(itemStackString, missingMetaValue);
			if (stack != null) {
				stacks.add(stack);
			}
		}

		return stacks;
	}

	public static Stack parseStackString(String stackString, int missingMetaValue) {
		if (stackString == null) {
			return null;
		}

		stackString = stackString.trim();
		if (stackString.isEmpty()) {
			return null;
		}

		String[] parts = stackString.split(":+");

		if (parts.length != 2 && parts.length != 3) {
			Proxies.log.warning("Stack string (" + stackString
					+ ") isn't formatted properly. Suitable formats are <modId>:<name>, <modId>:<name>:<meta> or <modId>:<name>:*, e.g. IC2:blockWall:*");
			return null;
		}

		String name = parts[0] + ':' + parts[1];
		int meta;

		if (parts.length == 2) {
			meta = missingMetaValue;
		} else {
			try {
				meta = parts[2].equals("*") ? OreDictionary.WILDCARD_VALUE
						: NumberFormat.getIntegerInstance().parse(parts[2]).intValue();
			} catch (ParseException e) {
				Proxies.log.warning("ItemStack string (" + stackString
						+ ") has improperly formatted metadata. Suitable metadata are integer values or *.");
				return null;
			}
		}

		return new Stack(name, meta);
	}
}
