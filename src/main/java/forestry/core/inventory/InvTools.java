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
package forestry.core.inventory;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.Optional;

import forestry.core.inventory.filters.ArrayStackFilter;
import forestry.core.inventory.filters.IStackFilter;
import forestry.core.inventory.filters.StackFilter;
import forestry.core.inventory.manipulators.InventoryManipulator;
import forestry.core.inventory.wrappers.ChestWrapper;
import forestry.core.inventory.wrappers.IInvSlot;
import forestry.core.inventory.wrappers.InventoryCopy;
import forestry.core.inventory.wrappers.InventoryIterator;
import forestry.core.inventory.wrappers.SidedInventoryMapper;
import forestry.core.utils.AdjacentTileCache;
import forestry.core.utils.StackUtils;
import forestry.plugins.PluginManager;

import buildcraft.api.transport.IPipeTile;

public abstract class InvTools {

	public static IInventory getInventoryFromTile(TileEntity tile, ForgeDirection side) {
		if (tile == null || !(tile instanceof IInventory)) {
			return null;
		}

		if (tile instanceof TileEntityChest) {
			TileEntityChest chest = (TileEntityChest) tile;
			return new ChestWrapper(chest);
		}
		return getInventory((IInventory) tile, side);
	}

	public static IInventory getInventory(IInventory inv, ForgeDirection side) {
		if (inv == null) {
			return null;
		}
		if (inv instanceof ISidedInventory) {
			inv = new SidedInventoryMapper((ISidedInventory) inv, side);
		}
		return inv;
	}

	public static ItemStack depleteItem(ItemStack stack) {
		if (stack.stackSize == 1) {
			return stack.getItem().getContainerItem(stack);
		} else {
			stack.splitStack(1);
			return stack;
		}
	}

	public static boolean isWildcard(ItemStack stack) {
		return isWildcard(stack.getItemDamage());
	}

	public static boolean isWildcard(int damage) {
		return damage == -1 || damage == OreDictionary.WILDCARD_VALUE;
	}

	/**
	 * A more robust item comparison function.
	 *
	 * Compares stackSize as well.
	 *
	 * Two null stacks will return true, unlike the other functions.
	 *
	 * This function is primarily intended to be used to track changes to an
	 * ItemStack.
	 *
	 * @param a An ItemStack
	 * @param b An ItemStack
	 * @return True if equal
	 */
	public static boolean isItemEqualStrict(ItemStack a, ItemStack b) {
		if (a == null && b == null) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		if (a.getItem() != b.getItem()) {
			return false;
		}
		if (a.stackSize != b.stackSize) {
			return false;
		}
		if (a.getItemDamage() != b.getItemDamage()) {
			return false;
		}
		if (a.stackTagCompound != null && !a.stackTagCompound.equals(b.stackTagCompound)) {
			return false;
		}
		return true;
	}

	/**
	 * A more robust item comparison function. Supports items with damage = -1
	 * matching any sub-type.
	 *
	 * @param a An ItemStack
	 * @param b An ItemStack
	 * @return True if equal
	 */
	public static boolean isItemEqual(ItemStack a, ItemStack b) {
		return isItemEqual(a, b, true, true);
	}

	public static boolean isItemEqual(final ItemStack a, final ItemStack b, final boolean matchDamage, final boolean matchNBT) {
		if (a == null || b == null) {
			return false;
		}
		if (a.getItem() != b.getItem()) {
			return false;
		}
		if (matchNBT && !ItemStack.areItemStackTagsEqual(a, b)) {
			return false;
		}
		if (matchDamage && a.getHasSubtypes()) {
			if (isWildcard(a) || isWildcard(b)) {
				return true;
			}
			if (a.getItemDamage() != b.getItemDamage()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if the item is equal to any one of several possible matches.
	 */
	public static boolean isItemEqual(ItemStack stack, ItemStack... matches) {
		for (ItemStack match : matches) {
			if (isItemEqual(stack, match)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Places an ItemStack in a destination IInventory. Will attempt to move as
	 * much of the stack as possible, returning any remainder.
	 *
	 * @param stack The ItemStack to put in the inventory.
	 * @param dest  The destination IInventory.
	 * @return Null if itemStack was completely moved, a new itemStack with
	 * remaining stackSize if part or none of the stack was moved.
	 */
	public static ItemStack moveItemStack(ItemStack stack, IInventory dest) {
		InventoryManipulator im = InventoryManipulator.get(dest);
		return im.addStack(stack);
	}

	/**
	 * Attempts to move an ItemStack from one inventory to another.
	 *
	 * @param source The source IInventory.
	 * @param dest   The destination IInventory.
	 * @return true if any items were moved
	 */
	public static boolean moveItemStack(IInventory source, IInventory dest) {
		InventoryManipulator im = InventoryManipulator.get(dest);
		for (IInvSlot slot : InventoryIterator.getIterable(source)) {
			ItemStack stack = slot.getStackInSlot();
			if (stack != null) {
				ItemStack remainder = im.addStack(stack);
				slot.setStackInSlot(remainder);
				return !isItemEqualStrict(stack, remainder);
			}
		}
		return false;
	}

	/**
	 * Attempts to move an ItemStack from one inventory to another.
	 *
	 * @param source       The source IInventory.
	 * @param destinations The destination IInventory.
	 * @return true if any items were moved
	 */
	public static boolean moveItemStack(IInventory source, Iterable<IInventory> destinations) {
		for (IInventory dest : destinations) {
			if (moveItemStack(source, dest)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes and returns a single item from the inventory.
	 *
	 * @param inv The inventory
	 * @return An ItemStack
	 */
	public static ItemStack removeOneItem(IInventory inv) {
		return removeOneItem(inv, StackFilter.ALL);
	}

	/**
	 * Removes and returns a single item from the inventory that matches the
	 * filter.
	 *
	 * @param inv    The inventory
	 * @param filter ItemStack to match against
	 * @return An ItemStack
	 */
	public static ItemStack removeOneItem(IInventory inv, ItemStack... filter) {
		return removeOneItem(inv, new ArrayStackFilter(filter));
	}

	/**
	 * Removes and returns a single item from the inventory that matches the
	 * filter.
	 *
	 * @param inv    The inventory
	 * @param filter EnumItemType to match against
	 * @return An ItemStack
	 */
	public static ItemStack removeOneItem(IInventory inv, IStackFilter filter) {
		InventoryManipulator im = InventoryManipulator.get(inv);
		return im.removeItem(filter);
	}

	/**
	 * Attempts to move a single item from the source inventory into a adjacent Buildcraft pipe.
	 * If the attempt fails, the source Inventory will not be modified.
	 *
	 * @param source    The source inventory
	 * @param tileCache The tile cache of the source block.
	 * @return true if an item was inserted, otherwise false.
	 */
	public static boolean moveOneItemToPipe(IInventory source, AdjacentTileCache tileCache) {
		return moveOneItemToPipe(source, tileCache, ForgeDirection.VALID_DIRECTIONS);
	}

	public static boolean moveOneItemToPipe(IInventory source, AdjacentTileCache tileCache, ForgeDirection[] directions) {
		if (PluginManager.Module.BUILDCRAFT_TRANSPORT.isEnabled()) {
			return internal_moveOneItemToPipe(source, tileCache, directions);
		}

		return false;
	}

	@Optional.Method(modid = "BuildCraftAPI|transport")
	private static boolean internal_moveOneItemToPipe(IInventory source, AdjacentTileCache tileCache, ForgeDirection[] directions) {
		IInventory invClone = new InventoryCopy(source);
		ItemStack stackToMove = removeOneItem(invClone);
		if (stackToMove == null) {
			return false;
		}
		if (stackToMove.stackSize <= 0) {
			return false;
		}

		List<Map.Entry<ForgeDirection, IPipeTile>> pipes = new ArrayList<Map.Entry<ForgeDirection, IPipeTile>>();
		boolean foundPipe = false;
		for (ForgeDirection side : directions) {
			TileEntity tile = tileCache.getTileOnSide(side);
			if (tile instanceof IPipeTile) {
				IPipeTile pipe = (IPipeTile) tile;
				if (pipe.getPipeType() == IPipeTile.PipeType.ITEM && pipe.isPipeConnected(side.getOpposite())) {
					pipes.add(new AbstractMap.SimpleEntry<ForgeDirection, IPipeTile>(side, pipe));
					foundPipe = true;
				}
			}
		}

		if (!foundPipe) {
			return false;
		}

		int choice = tileCache.getSource().getWorldObj().rand.nextInt(pipes.size());
		Map.Entry<ForgeDirection, IPipeTile> pipe = pipes.get(choice);
		if (pipe.getValue().injectItem(stackToMove, false, pipe.getKey().getOpposite(), null) > 0) {
			if (removeOneItem(source, stackToMove) != null) {
				pipe.getValue().injectItem(stackToMove, true, pipe.getKey().getOpposite(), null);
				return true;
			}
		}
		return false;
	}

	/* REMOVAL */

	/**
	 * Removes a set of items from an inventory.
	 * Removes the exact items first if they exist, and then removes crafting equivalents.
	 * If the inventory doesn't have all the required items, returns false without removing anything.
	 * If stowContainer is true, items with containers will have their container stowed.
	 */
	public static boolean removeSets(IInventory inventory, int count, ItemStack[] set, EntityPlayer player, boolean stowContainer, boolean oreDictionary) {
		return removeSets(inventory, count, set, 0, inventory.getSizeInventory(), player, stowContainer, oreDictionary, false) != null;
	}

	public static boolean removeSets(IInventory inventory, int count, ItemStack[] set, int firstSlotIndex, int slotCount, EntityPlayer player, boolean stowContainer, boolean oreDictionary) {
		return removeSets(inventory, count, set, firstSlotIndex, slotCount, player, stowContainer, oreDictionary, false) != null;
	}

	public static ItemStack[] removeSets(IInventory inventory, int count, ItemStack[] set, int firstSlotIndex, int slotCount, EntityPlayer player, boolean stowContainer, boolean oreDictionary, boolean craftingTools) {

		ItemStack[] removed = new ItemStack[set.length];
		ItemStack[] stock = getStacks(inventory, firstSlotIndex, slotCount);

		if (StackUtils.containsSets(set, stock, oreDictionary, craftingTools) < count) {
			return null;
		}

		for (int i = 0; i < set.length; i++) {
			if (set[i] == null) {
				continue;
			}
			ItemStack stackToRemove = set[i].copy();
			stackToRemove.stackSize *= count;

			// try to remove the exact stack first
			ItemStack removedStack = removeStack(inventory, stackToRemove, firstSlotIndex, slotCount, player, stowContainer, false, false);
			if (removedStack == null) {
				// remove crafting equivalents next
				removedStack = removeStack(inventory, stackToRemove, firstSlotIndex, slotCount, player, stowContainer, oreDictionary, craftingTools);
			}

			removed[i] = removedStack;
		}
		return removed;
	}

	/**
	 * Private Helper for removeSetsFromInventory. Assumes removal is possible.
	 */
	private static ItemStack removeStack(IInventory inventory, ItemStack stackToRemove, int firstSlotIndex, int slotCount, EntityPlayer player, boolean stowContainer, boolean oreDictionary, boolean craftingTools) {
		for (int j = firstSlotIndex; j < firstSlotIndex + slotCount; j++) {
			ItemStack stackInSlot = inventory.getStackInSlot(j);
			if (stackInSlot == null) {
				continue;
			}

			if (!StackUtils.isCraftingEquivalent(stackToRemove, stackInSlot, oreDictionary, craftingTools)) {
				continue;
			}

			ItemStack removed = inventory.decrStackSize(j, stackToRemove.stackSize);
			stackToRemove.stackSize -= removed.stackSize;

			if (stowContainer && stackToRemove.getItem().hasContainerItem(stackToRemove)) {
				StackUtils.stowContainerItem(removed, inventory, j, player);
			}

			if (stackToRemove.stackSize == 0) {
				return removed;
			}
		}
		return null;
	}

	/* CONTAINS */

	public static boolean contains(IInventory inventory, ItemStack[] query) {
		return contains(inventory, query, 0, inventory.getSizeInventory());
	}

	public static boolean contains(IInventory inventory, ItemStack[] query, int startSlot, int slots) {
		ItemStack[] stock = getStacks(inventory, startSlot, slots);
		return StackUtils.containsSets(query, stock) > 0;
	}

	public static boolean containsPercent(IInventory inventory, float percent) {
		return containsPercent(inventory, percent, 0, inventory.getSizeInventory());
	}

	public static boolean containsPercent(IInventory inventory, float percent, int slot1, int length) {
		int amount = 0;
		int stackMax = 0;
		for (ItemStack itemStack : getStacks(inventory, slot1, length)) {
			if (itemStack == null) {
				stackMax += 64;
				continue;
			}

			amount += itemStack.stackSize;
			stackMax += itemStack.getMaxStackSize();
		}
		if (stackMax == 0) {
			return false;
		}
		return ((float) amount / (float) stackMax) >= percent;
	}

	public static ItemStack[] getStacks(IInventory inventory) {
		ItemStack[] stacks = new ItemStack[inventory.getSizeInventory()];
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			stacks[i] = inventory.getStackInSlot(i);
		}
		return stacks;
	}

	public static ItemStack[] getStacks(IInventory inventory, int slot1, int length) {
		ItemStack[] result = new ItemStack[length];
		for (int i = slot1; i < slot1 + length; i++) {
			result[i - slot1] = inventory.getStackInSlot(i);
		}
		return result;
	}

	public static boolean tryAddStacksCopy(IInventory inventory, ItemStack[] stacks, int startSlot, int slots, boolean all) {

		for (ItemStack stack : stacks) {
			if (stack == null) {
				continue;
			}

			if (!tryAddStack(inventory, stack.copy(), startSlot, slots, all)) {
				return false;
			}
		}

		return true;
	}

	public static boolean tryAddStack(IInventory inventory, ItemStack stack, boolean all) {
		return tryAddStack(inventory, stack, 0, inventory.getSizeInventory(), all, true);
	}

	public static boolean tryAddStack(IInventory inventory, ItemStack stack, boolean all, boolean doAdd) {
		return tryAddStack(inventory, stack, 0, inventory.getSizeInventory(), all, doAdd);
	}

	/**
	 * Tries to add a stack to the specified slot range.
	 */
	public static boolean tryAddStack(IInventory inventory, ItemStack stack, int startSlot, int slots, boolean all) {
		return tryAddStack(inventory, stack, startSlot, slots, all, true);
	}

	public static boolean tryAddStack(IInventory inventory, ItemStack stack, int startSlot, int slots, boolean all, boolean doAdd) {
		int added = addStack(inventory, stack, startSlot, slots, doAdd);
		if (all) {
			return added == stack.stackSize;
		} else {
			return added > 0;
		}
	}

	public static int addStack(IInventory inventory, ItemStack stack, boolean doAdd) {
		return addStack(inventory, stack, 0, inventory.getSizeInventory(), doAdd);
	}

	public static int addStack(IInventory inventory, ItemStack stack, int startSlot, int slots, boolean doAdd) {

		int added = 0;
		// Add to existing stacks first
		for (int i = startSlot; i < startSlot + slots; i++) {

			ItemStack inventoryStack = inventory.getStackInSlot(i);
			// Empty slot. Add
			if (inventoryStack == null || inventoryStack.getItem() == null) {
				continue;
			}

			// Already occupied by different item, skip this slot.
			if (!inventoryStack.isStackable()) {
				continue;
			}
			if (!inventoryStack.isItemEqual(stack)) {
				continue;
			}
			if (!ItemStack.areItemStackTagsEqual(inventoryStack, stack)) {
				continue;
			}

			int remain = stack.stackSize - added;
			int space = inventoryStack.getMaxStackSize() - inventoryStack.stackSize;
			// No space left, skip this slot.
			if (space <= 0) {
				continue;
			}
			// Enough space
			if (space >= remain) {
				if (doAdd) {
					inventoryStack.stackSize += remain;
				}
				return stack.stackSize;
			}

			// Not enough space
			if (doAdd) {
				inventoryStack.stackSize = inventoryStack.getMaxStackSize();
			}

			added += space;
		}

		if (added >= stack.stackSize) {
			return added;
		}

		for (int i = startSlot; i < startSlot + slots; i++) {
			if (inventory.getStackInSlot(i) != null) {
				continue;
			}

			if (doAdd) {
				inventory.setInventorySlotContents(i, stack.copy());
				inventory.getStackInSlot(i).stackSize = stack.stackSize - added;
			}
			return stack.stackSize;

		}

		return added;
	}
}
